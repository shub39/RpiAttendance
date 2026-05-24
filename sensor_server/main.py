import os
import time
import socket
import asyncio
import logging
import json
import subprocess
import uvicorn

from contextlib import asynccontextmanager
from concurrent.futures import ThreadPoolExecutor
from datetime import datetime
from typing import List, Optional
from fastapi import FastAPI, HTTPException, WebSocket, WebSocketDisconnect
from fastapi.concurrency import run_in_threadpool
from pydantic import BaseModel
from display import draw
from face_capture import FaceCapture
from face_recogniser import FaceRecognizer
from keypad import read_key

logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    datefmt='%Y-%m-%d %H:%M:%S',
)

SERVER_HOST = "0.0.0.0"
SERVER_PORT = 8000

PROCESS_TIMEOUT = 10
FACE_POLL_INTERVAL = 0.15
FACE_DEDUP_WINDOW = 30
EVENT_HISTORY_LIMIT = 100

KEYPAD_POLL_INTERVAL = 0.1
METADATA_DIR = "faculty_metadata"

def get_local_ip():
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    try:
        s.connect(("8.8.8.8", 80))
        return s.getsockname()[0]
    except Exception:
        return "127.0.0.1"
    finally:
        s.close()

class Hardware:
    def __init__(self):
        try:
            self.face_cap = FaceCapture()
            self.face_rec = FaceRecognizer()
            self.ok = True
        except Exception as e:
            logging.error(f"[HARDWARE INIT FAILED] {e}")
            self.ok = False

    def assert_ok(self):
        if not self.ok:
            raise RuntimeError("Hardware not initialized")

hw = Hardware()
executor = ThreadPoolExecutor(max_workers=3)
face_detector = None
keypad_task = None

@asynccontextmanager
async def lifespan(app: FastAPI):
    if not hw.ok:
        raise RuntimeError("Hardware not initialized")
    global face_detector, keypad_task
    face_detector = FaceDetectionService(hw.face_rec)
    face_detector.start()
    keypad_task = asyncio.create_task(keypad_worker())
    yield
    if keypad_task:
        keypad_task.cancel()
    if face_detector:
        await face_detector.stop()
    executor.shutdown(wait=False)

app = FastAPI(title="RPi Sensor Server", lifespan=lifespan)

class DisplayRequest(BaseModel):
    lines: List[str]

class FaceEnrollRequest(BaseModel):
    id: str
    name: str
    dept: str
    designation: str

class FaceDeleteRequest(BaseModel):
    id: str

class Faculty(BaseModel):
    id: str
    name: str
    dept: str
    designation: str

class FaceEvent(BaseModel):
    faculty: Faculty
    login_time: str
    timestamp: float

class FaceEventsResponse(BaseModel):
    events: List[FaceEvent]

class LatestFaceEventResponse(BaseModel):
    event: Optional[FaceEvent]

class FaceDetectionService:
    def __init__(self, recognizer: FaceRecognizer):
        self.recognizer = recognizer
        self.pause_event = asyncio.Event()
        self.pause_event.set()
        self.camera_released = asyncio.Event()
        self.camera_released.set()
        self._task = None
        self._lock = asyncio.Lock()
        self._history = []
        self._last_seen = {}
        self._websockets = set()

    def start(self):
        self._task = asyncio.create_task(self._run())

    async def stop(self):
        if self._task:
            self._task.cancel()
            try:
                await self._task
            except asyncio.CancelledError:
                pass

    async def pause(self):
        self.pause_event.clear()
        await asyncio.wait_for(self.camera_released.wait(), timeout=3)

    async def resume(self):
        self.pause_event.set()

    async def latest(self):
        async with self._lock:
            return self._history[-1] if self._history else None

    async def events_since(self, since: Optional[float]):
        async with self._lock:
            if since is None:
                return list(self._history)
            return [event for event in self._history if event["timestamp"] > since]

    async def add_websocket(self, websocket: WebSocket):
        await websocket.accept()
        self._websockets.add(websocket)

    def remove_websocket(self, websocket: WebSocket):
        self._websockets.discard(websocket)

    async def _run(self):
        cam = None
        try:
            while True:
                if not self.pause_event.is_set():
                    if cam is not None:
                        await run_in_threadpool(cam.close)
                        cam = None
                        self.camera_released.set()
                        logging.info("Continuous face detection paused")

                await self.pause_event.wait()

                if cam is None:
                    self.camera_released.clear()
                    cam = await run_in_threadpool(self.recognizer._start_camera)
                    logging.info("Continuous face detection started")

                frame = await run_in_threadpool(cam.capture_array)
                faculty_id = await run_in_threadpool(self.recognizer.recognize_frame, frame)

                if faculty_id:
                    await self._handle_match(faculty_id)

                await asyncio.sleep(FACE_POLL_INTERVAL)
        except asyncio.CancelledError:
            raise
        except Exception as e:
            logging.exception("Face detection loop failed: %s", e)
        finally:
            if cam is not None:
                await run_in_threadpool(cam.close)
                self.camera_released.set()
                logging.info("Continuous face detection stopped")

    async def _handle_match(self, faculty_id: str):
        now = time.time()
        if now - self._last_seen.get(faculty_id, 0) < FACE_DEDUP_WINDOW:
            return

        faculty = load_faculty(faculty_id)
        if faculty is None:
            logging.warning("Recognized face %s has no faculty metadata", faculty_id)
            return

        self._last_seen[faculty_id] = now
        login_time = datetime.now().strftime("%H:%M:%S")
        event = {
            "faculty": faculty,
            "login_time": login_time,
            "timestamp": now,
        }

        async with self._lock:
            self._history.append(event)
            self._history = self._history[-EVENT_HISTORY_LIMIT:]

        await run_in_threadpool(
            draw,
            [faculty["name"], faculty["id"], login_time],
        )
        await self._broadcast(event)
        await asyncio.sleep(3)

    async def _broadcast(self, event):
        stale = []
        for websocket in list(self._websockets):
            try:
                await websocket.send_json(event)
            except Exception:
                stale.append(websocket)

        for websocket in stale:
            self.remove_websocket(websocket)

def faculty_metadata_path(faculty_id: str):
    safe_id = faculty_id.replace("/", "_").replace("\\", "_")
    return os.path.join(METADATA_DIR, f"{safe_id}.json")

def save_faculty(faculty: dict):
    os.makedirs(METADATA_DIR, exist_ok=True)
    with open(faculty_metadata_path(faculty["id"]), "w", encoding="utf-8") as f:
        json.dump(faculty, f)

def load_faculty(faculty_id: str):
    path = faculty_metadata_path(faculty_id)
    if not os.path.isfile(path):
        return None

    with open(path, "r", encoding="utf-8") as f:
        return json.load(f)

async def keypad_worker():
    last_key = None
    last_key_time = 0

    while True:
        key = await run_in_threadpool(read_key)
        now = time.time()

        if key is not None and (key != last_key or now - last_key_time > 1):
            last_key = key
            last_key_time = now

            if key == "1":
                draw(["SENSOR SERVER", f"{get_local_ip()}:{SERVER_PORT}"])
            elif key == "A":
                draw(["Shutting Down"])
                subprocess.Popen(["sudo", "shutdown", "now"])

        await asyncio.sleep(KEYPAD_POLL_INTERVAL)

async def with_timeout(func, *args):
    return await asyncio.wait_for(
        run_in_threadpool(func, *args),
        timeout=PROCESS_TIMEOUT
    )

@app.post("/display")
async def show_text(req: DisplayRequest):
    hw.assert_ok()
    try:
        await run_in_threadpool(draw, req.lines)
        return {"status": "ok"}
    except Exception as e:
        raise HTTPException(500, str(e))


@app.post("/face/enroll")
async def enroll_face(req: FaceEnrollRequest):
    hw.assert_ok()
    try:
        await face_detector.pause()
        faculty = {
            "id": req.id,
            "name": req.name,
            "dept": req.dept,
            "designation": req.designation,
        }
        save_faculty(faculty)
        result = await with_timeout(hw.face_cap.capture_and_encode, req.id, 5, PROCESS_TIMEOUT)

        if not result:
            raise HTTPException(400, "No face detected")

        await run_in_threadpool(hw.face_rec.reload_encodings)
        return {"status": "ok"}
    except HTTPException as e:
        raise HTTPException(e.status_code, e.detail)
    except Exception as e:
        raise HTTPException(500, str(e))
    finally:
        await face_detector.resume()


@app.post("/face/recognize")
async def recognize_face():
    hw.assert_ok()
    try:
        await face_detector.pause()
        match = await with_timeout(hw.face_rec.recognize)
        return {"match": match}
    except Exception as e:
        raise HTTPException(500, str(e))
    finally:
        await face_detector.resume()

@app.get("/face/latest")
async def latest_face_event():
    hw.assert_ok()
    return LatestFaceEventResponse(event=await face_detector.latest())

@app.get("/face/events")
async def face_events(since: Optional[float] = None):
    hw.assert_ok()
    return FaceEventsResponse(events=await face_detector.events_since(since))

@app.websocket("/face/ws")
async def face_websocket(websocket: WebSocket):
    await face_detector.add_websocket(websocket)
    try:
        while True:
            await websocket.receive_text()
    except WebSocketDisconnect:
        face_detector.remove_websocket(websocket)


@app.post("/face/delete")
async def delete_face(req: FaceDeleteRequest):
    hw.assert_ok()
    path = os.path.join("encodings", f"{req.id}.pkl")
    metadata_path = faculty_metadata_path(req.id)

    if not os.path.isfile(path):
        raise HTTPException(404, "Face ID not found")

    try:
        os.remove(path)
        if os.path.isfile(metadata_path):
            os.remove(metadata_path)
        await run_in_threadpool(hw.face_rec.reload_encodings)
        return {"status": "ok"}
    except Exception as e:
        raise HTTPException(500, str(e))

@app.get("/keypad")
async def keypad(timeout: float = 5.0):
    hw.assert_ok()
    deadline = time.time() + timeout

    while time.time() < deadline:
        key = await run_in_threadpool(read_key)
        if key is not None:
            return {"key": key}
        await asyncio.sleep(KEYPAD_POLL_INTERVAL)

    return {"key": None}

@app.get("/status")
async def status():
    hw.assert_ok()
    return {"status": "ok", "ip": get_local_ip()}

if __name__ == "__main__":
    ip = get_local_ip()
    print(f"API: http://{ip}:{SERVER_PORT}/docs")

    uvicorn.run(
        app,
        host=SERVER_HOST,
        port=SERVER_PORT,
        workers=1,
        log_level="info"
    )
