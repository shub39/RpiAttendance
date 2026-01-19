import os
import pickle
import time
import logging
import numpy as np
import face_recognition

from picamera2 import Picamera2, Preview

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s - %(levelname)s - %(message)s",
)

class FaceRecognizer:
    def __init__(self, dataset_dir="encodings"):
        self.dataset_dir = dataset_dir
        self.known_encodings = []
        self.known_names = []
        self.reload_encodings()

    def reload_encodings(self):
        self.known_encodings.clear()
        self.known_names.clear()

        os.makedirs(self.dataset_dir, exist_ok=True)

        for file in os.listdir(self.dataset_dir):
            if not file.endswith(".pkl"):
                continue

            name = os.path.splitext(file)[0]
            path = os.path.join(self.dataset_dir, file)

            try:
                with open(path, "rb") as f:
                    encs = pickle.load(f)

                for enc in encs:
                    self.known_encodings.append(enc)
                    self.known_names.append(name)

            except Exception as e:
                logging.warning(f"Failed loading {file}: {e}")

        logging.info(
            f"Loaded {len(self.known_encodings)} encodings "
            f"for {len(set(self.known_names))} identities"
        )

    def _start_camera(self):
        cam = Picamera2()
        config = cam.create_still_configuration(
            main={"size": (640, 480), "format": "RGB888"}
        )
        cam.configure(config)
        cam.start_preview(Preview.NULL)
        cam.start()
        return cam

    def _match_face(self, encoding, tolerance=0.5):
        distances = face_recognition.face_distance(
            self.known_encodings, encoding
        )

        if len(distances) == 0:
            return None

        best_idx = np.argmin(distances)
        best_distance = distances[best_idx]

        logging.debug(
            f"Best match: {self.known_names[best_idx]} "
            f"(distance={best_distance:.3f})"
        )

        if best_distance <= tolerance:
            return self.known_names[best_idx]

        return None

    def recognize(self, timeout=10, tolerance=0.5):
        if not self.known_encodings:
            logging.error("No known face encodings loaded")
            return None

        cam = self._start_camera()
        start_time = time.time()

        logging.info("Starting face recognition")

        try:
            while time.time() - start_time < timeout:
                frame = cam.capture_array()
                rgb = frame

                locations = face_recognition.face_locations(rgb)
                if not locations:
                    continue

                encodings = face_recognition.face_encodings(rgb, locations)

                for enc in encodings:
                    name = self._match_face(enc, tolerance)
                    if name:
                        logging.info(f"[MATCH] {name}")
                        return name

            logging.info("No match found")
            return None

        finally:
            cam.close()
            logging.info("Camera released")

if __name__ == "__main__":
    recognizer = FaceRecognizer()
    recognizer.reload_encodings()
    result = recognizer.recognize()
    print(f"Recognized face: {result}")