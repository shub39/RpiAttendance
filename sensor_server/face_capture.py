import face_recognition
import os
import pickle
import time
import logging

from display import draw
from picamera2 import Picamera2, Preview

logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    datefmt='%Y-%m-%d %H:%M:%S',
)

class FaceCapture:
    def __init__(self, dataset_dir="encodings"):
        self.dataset_dir = dataset_dir

    def capture_and_encode(self, name, count=5, timeout=10):
        """
        Captures and encodes faces from a camera.

        Args:
            name (str): The name of the person whose face is being captured.
            count (int): The number of face encodings to capture.
            timeout (int): The maximum time to wait for face detection.

        Returns:
            bool: True if the face capture was successful, False otherwise.
        """
        logging.info("Starting Face Capture")
        os.makedirs(self.dataset_dir, exist_ok=True)
        self.cam = Picamera2()
        config = self.cam.create_still_configuration(
            main={"size": (640, 480), "format": "RGB888"}
        )
        self.cam.configure(config)
        self.cam.start_preview(Preview.NULL)
        self.cam.start()

        encodings = []

        start_time = time.time()

        while len(encodings) < count:
            if time.time() - start_time >= timeout:
                self.cam.close()
                return False

            frame = self.cam.capture_array()

            rgb = frame

            face_locations = face_recognition.face_locations(rgb)

            if not face_locations:
                logging.info("No Face Detected")
                draw(["look at ", "the camera"])
                time.sleep(0.1)
                continue

            face_encs = face_recognition.face_encodings(rgb, face_locations)

            if len(face_encs) == 1:
                encodings.append(face_encs[0])
                logging.info(f"Captured {len(encodings)}/{count}")
                draw(["face captured", f"{len(encodings)}/{count}"])
            else:
                logging.info("Encoding failed, skipping")

        self.cam.close()

        save_path = os.path.join(self.dataset_dir, f"{name}.pkl")
        with open(save_path, "wb") as f:
            pickle.dump(encodings, f)

        logging.info(f"Saved encodings for {name} to {save_path}")
        return True

if __name__ == "__main__":
    face_capture = FaceCapture()
    result = face_capture.capture_and_encode("user", count=5, timeout=10)
    print(result)