import os
import pickle
import time
import logging
from collections import Counter

import face_recognition
import numpy as np
from picamera2 import Picamera2, Preview

logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    datefmt='%Y-%m-%d %H:%M:%S',
)

class FaceRecognizer:
    def __init__(self, dataset_dir="encodings"):
        self.dataset_dir = dataset_dir
        self.known_encodings = []
        self.known_names = []
        self.reload_encodings()

    def reload_encodings(self):
        """Load all face encodings from the dataset directory."""
        if not os.path.exists(self.dataset_dir):
            os.makedirs(self.dataset_dir)

        for file in os.listdir(self.dataset_dir):
            if file.endswith(".pkl"):
                name = os.path.splitext(file)[0]
                path = os.path.join(self.dataset_dir, file)
                try:
                    with open(path, "rb") as f:
                        encs = pickle.load(f)
                        if isinstance(encs, list) and all(isinstance(e, np.ndarray) for e in encs):
                            self.known_encodings.extend(encs)
                            self.known_names.extend([name] * len(encs))
                        else:
                            logging.warning(f"Skipping corrupted encoding file: {file}")
                except Exception as e:
                    logging.warning(f"Failed to load {file}: {e}")

        logging.info(f"Loaded {len(self.known_encodings)} encodings for {len(set(self.known_names))} identities.")

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
        matches = face_recognition.compare_faces(self.known_encodings, encoding, tolerance)
        matched_names = [name for match, name in zip(matches, self.known_names) if match]
        if matched_names:
            return Counter(matched_names).most_common(1)[0][0]
        return None

    def recognize(self, timeout=10, tolerance=0.99):
        """
        Recognize a known face from camera within the timeout period.

        Args:
            timeout (int): The maximum time to wait for face detection.
            tolerance (float): The tolerance for face recognition.

        Returns:
            str: The name of the recognized face, or None if no face was recognized.
        """
        if not self.known_encodings:
            logging.error("No known face encodings loaded.")
            return None

        cam = self._start_camera()
        logging.info("Camera started for recognition.")

        start_time = time.time()
        try:
            while time.time() - start_time < timeout:
                frame = cam.capture_array()
                rgb = frame
                boxes = face_recognition.face_locations(rgb)

                if not boxes:
                    continue

                encodings = face_recognition.face_encodings(rgb, boxes)
                for encoding in encodings:
                    name = self._match_face(encoding, tolerance)
                    if name:
                        logging.info(f"[MATCH] Recognized: {name}")
                        return name

            logging.info("No match found within timeout.")
            return None

        except Exception as e:
            logging.error(f"Recognition error: {e}")
            return None

        finally:
            cam.close()
            logging.info("Camera released.")

if __name__ == "__main__":
    recognizer = FaceRecognizer()
    recognizer.reload_encodings()
    result = recognizer.recognize()
    print(f"Recognized face: {result}")