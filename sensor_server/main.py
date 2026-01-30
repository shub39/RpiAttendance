import os
import time
import socket
import asyncio
import logging
import uvicorn

from contextlib import asynccontextmanager
from concurrent.futures import ThreadPoolExecutor
from typing import List
from fastapi import FastAPI, HTTPException
from fastapi.concurrency import run_in_threadpool
from pydantic import BaseModel
from display import draw
from fingerprint import FingerprintSensor, FingerprintError
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

KEYPAD_POLL_INTERVAL = 0.1

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
            self.fp = FingerprintSensor()
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

@asynccontextmanager
async def lifespan(app: FastAPI):
    if not hw.ok:
        raise RuntimeError("Hardware not initialized")
    yield
    executor.shutdown(wait=False)

app = FastAPI(title="RPi Sensor Server", lifespan=lifespan)

class DisplayRequest(BaseModel):
    lines: List[str]

class FaceEnrollRequest(BaseModel):
    name: str

class FaceDeleteRequest(BaseModel):
    name: str

class FingerprintDeleteRequest(BaseModel):
    id: int

async def with_timeout(func, *args):
    return await asyncio.wait_for(
        run_in_threadpool(func, *args),
        timeout=HARDWARE_TIMEOUT
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
        result = await with_timeout(hw.face_cap.capture_and_encode, req.name, 5, PROCESS_TIMEOUT)

        if not result:
            raise HTTPException(400, "No face detected")

        await run_in_threadpool(hw.face_rec.reload_encodings)
        return {"status": "ok"}
    except HTTPException as e:
        raise HTTPException(e.status_code, e.detail)
    except Exception as e:
        raise HTTPException(500, str(e))


@app.post("/face/recognize")
async def recognize_face():
    hw.assert_ok()
    try:
        match = await with_timeout(hw.face_rec.recognize)
        return {"match": match}
    except Exception as e:
        raise HTTPException(500, str(e))


@app.post("/fingerprint/enroll")
async def enroll_fingerprint():
    hw.assert_ok()
    try:
        idx = await with_timeout(hw.fp.capture_finger, PROCESS_TIMEOUT)
        return {"index": idx}
    except FingerprintError as e:
        raise HTTPException(400, str(e))
    except Exception as e:
        raise HTTPException(500, str(e))


@app.post("/fingerprint/search")
async def search_fingerprint():
    hw.assert_ok()
    try:
        idx = await with_timeout(hw.fp.search_fingerprint)
        return {"index": idx if idx != -1 else None}
    except FingerprintError as e:
        raise HTTPException(400, str(e))
    except Exception as e:
        raise HTTPException(500, str(e))


@app.post("/fingerprint/delete")
async def delete_fingerprint(req: FingerprintDeleteRequest):
    hw.assert_ok()
    try:
        await run_in_threadpool(hw.fp.delete_fingerprint, req.id)
        return {"status": "ok"}
    except Exception as e:
        raise HTTPException(500, str(e))


@app.post("/face/delete")
async def delete_face(req: FaceDeleteRequest):
    hw.assert_ok()
    path = os.path.join("encodings", f"{req.name}.pkl")

    if not os.path.isfile(path):
        raise HTTPException(404, "Face ID not found")

    try:
        os.remove(path)
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