import uvicorn
import socket
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List

# Import your existing modules
from display import draw
from fingerprint import FingerprintSensor, FingerprintError
from face_capture import FaceCapture
from face_recogniser import FaceRecognizer

# ==========================================
# CONFIGURATION
# ==========================================
SERVER_HOST = "0.0.0.0"  # Listen on all interfaces
SERVER_PORT = 8000       # Port to modify

# ==========================================
# IP ADDRESS HELPER FUNCTION
# ==========================================
def get_local_ip():
    """Attempts to determine the non-local IP address of the machine."""
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    try:
        # Connect to an external server (doesn't actually send data)
        # This forces the kernel to pick the best interface for routing
        s.connect(("8.8.8.8", 80))
        IP = s.getsockname()[0]
    except Exception:
        # Fallback for systems without network connectivity
        IP = '127.0.0.1'
    finally:
        s.close()
    return IP

# ==========================================
# GLOBAL HARDWARE INSTANCES
# Initialize sensors once to keep connections alive
# ==========================================
try:
    fp_sensor = FingerprintSensor()
    face_cap = FaceCapture()
    face_rec = FaceRecognizer()
except Exception as e:
    print(f"CRITICAL HARDWARE ERROR: {e}")
    # We continue so the server starts, but endpoints may fail
    pass

app = FastAPI(title="RPi Sensor Server")

# ==========================================
# DATA MODELS (Request Bodies)
# ==========================================

class DisplayRequest(BaseModel):
    lines: List[str]
    duration: float = 0

class FaceEnrollRequest(BaseModel):
    name: str

# ==========================================
# ENDPOINTS (Same as before)
# ==========================================

@app.post("/display")
def show_text(req: DisplayRequest):
    """Display text on the OLED screen."""
    try:
        draw(req.lines, req.duration)
        return {"status": "success", "message": "Text displayed"}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/face/enroll")
def enroll_face(req: FaceEnrollRequest):
    """Capture and encode a new face."""
    try:
        face_cap.capture_and_encode(req.name)
        face_rec._load_encodings()
        return {"status": "success", "message": f"Face enrolled successfully for {req.name}"}
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Face capture failed: {str(e)}")

@app.post("/face/recognize")
def recognize_face():
    """Take a picture and attempt to recognize a face."""
    try:
        match_name = face_rec.recognize(timeout=10, tolerance=0.5)
        if match_name:
            return {"status": "success", "match": match_name}
        else:
            return {"status": "success", "match": None}
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Recognition failed: {str(e)}")

@app.post("/fingerprint/enroll")
def enroll_fingerprint():
    """Start the fingerprint enrollment process."""
    try:
        position = fp_sensor.capture_finger()
        return {"status": "success", "index": position}
    except FingerprintError as e:
        raise HTTPException(status_code=500, detail=f"Fingerprint sensor error: {str(e)}")
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/fingerprint/search")
def search_fingerprint():
    """Scan for a fingerprint and return the index if found."""
    try:
        position = fp_sensor.search_fingerprint(timeout=10)
        if position is not None and position != -1:
            return {"status": "success", "index": position}
        else:
            return {"status": "success", "index": None}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/fingerprint/clear")
def clear_fingerprints():
    """DANGER: Clears all stored fingerprints."""
    try:
        fp_sensor.clear_fingerprints()
        return {"status": "success", "message": "All fingerprints cleared"}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


# ==========================================
# RUNNER
# ==========================================
if __name__ == "__main__":

    local_ip = get_local_ip()

    print("Starting Sensor Server...")
    print(f"Local IP: {local_ip}")
    print(f"Access API at: http://{local_ip}:{SERVER_PORT}/docs")

    try:
        draw(["RPi Sensor", "Starting..."], sleep_seconds=1)
        draw([f"IP: {local_ip}", f"Port: {SERVER_PORT}"], sleep_seconds=2)
    except Exception as e:
        print(f"Warning: Could not draw to display. Error: {e}")

    uvicorn.run(app, host=SERVER_HOST, port=SERVER_PORT)
