from picamera2 import Picamera2, Preview
import face_recognition
import os
import pickle
import time

from display import draw

class FaceCapture:
    def __init__(self, dataset_dir="encodings"):
        self.dataset_dir = dataset_dir

    def capture_and_encode(self, name, count=10):
        print("[INFO] Starting Face Capture")
        os.makedirs(self.dataset_dir, exist_ok=True)
        self.cam = Picamera2()
        config = self.cam.create_still_configuration(
            main={"size": (640, 480), "format": "RGB888"}
        )
        self.cam.configure(config)
        self.cam.start_preview(Preview.NULL)
        self.cam.start()

        encodings = []
        time.sleep(0.2)

        while len(encodings) < count:
            frame = self.cam.capture_array()

            rgb = frame

            face_locations = face_recognition.face_locations(rgb)

            if not face_locations:
                print("[INFO] No Face Detected")
                draw(["look at ", "the camera"])
                time.sleep(0.2)
                continue

            face_encs = face_recognition.face_encodings(rgb, face_locations)

            if len(face_encs) == 1:
                encodings.append(face_encs[0])
                print(f"[INFO] Captured {len(encodings)}/{count}")
                draw(["face captured", f"{len(encodings)}/{count}"])
            else:
                print("[INFO] Encoding failed, skipping")

        self.cam.close()

        save_path = os.path.join(self.dataset_dir, f"{name}.pkl")
        with open(save_path, "wb") as f:
            pickle.dump(encodings, f)

        print(f"[DONE] Saved encodings for {name} to {save_path}")
        draw(["done!!"])

