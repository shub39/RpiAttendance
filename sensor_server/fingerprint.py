import logging
from pyfingerprint.pyfingerprint import PyFingerprint
import time

from display import draw

logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    datefmt='%Y-%m-%d %H:%M:%S',
)

# exception raised by the sensor/ communication
class FingerprintError(Exception):
    def __init__(self, message):
        super().__init__(message)

# class to manage the fingerprint sensor
class FingerprintSensor:
    def __init__(self):
        """Initialize the sensor"""
        try:
            self.f = PyFingerprint("/dev/ttyUSB0", 57600, 0xFFFFFFFF, 0x00000000)
        except Exception as e:
            logging.error("can't initialize sensor")
            raise FingerprintError(e)

        logging.info("Sensor initialised successfully")

    def capture_finger(self):
        """Capture a fingerprint and store it if its unique"""
        logging.info("Capturing fingerprint. Stored fingerprints %d", self.f.getTemplateCount())

        try:
            logging.info("Please place your finger on the sensor.")
            draw(["Place your", "finger"])
            while not self.f.readImage():
                time.sleep(0.5)
                pass

            self.f.convertImage(0x01)
            result = self.f.searchTemplate()
            position_number = result[0]

            if position_number >= 0:
                logging.warning("Fingerprint already exists at position #%d.", position_number + 1)
                draw(["Fingerprint", "already exists"])
                return self._retry_capture()

            logging.info("Remove your finger.")
            draw(["Remove your", "finger"])
            while self.f.readImage():
                time.sleep(0.5)
                pass

            logging.info("Place your finger again.")
            draw(["Place your", "finger again"])
            while not self.f.readImage():
                time.sleep(0.5)
                pass

            self.f.convertImage(0x02)

            if self.f.compareCharacteristics() == 0:
                logging.warning("Fingerprints do not match.")
                draw(["Fingerprints", "do not match"])
                return self._retry_capture()

            self.f.createTemplate()
            position = self.f.storeTemplate()
            logging.info("Fingerprint stored successfully at position #%d.", position)
            draw(["Fingerprint", "stored successfully"])
            return position

        except Exception as e:
            logging.error("Error during fingerprint capture: %s", e)
            draw(["Error during", "fingerprint capture"])
            raise FingerprintError(e)

    def _retry_capture(self):
        """Ask the user if they want to retry the fingerprint capture."""
        choice = input("Do you want to try again? (y/n): ").strip().lower()
        if choice == "y":
            return self.capture_finger()
        else:
            logging.info("Fingerprint capture process aborted by the user.")
            return None

    def delete_fingerprint(self, index):
        """Delete a specific fingerprint"""
        try:
            self.f.deleteTemplate(index)
            logging.info(f"Deleted template at: {index}")
        except Exception as e:
            logging.error("Error deleting template")
            raise FingerprintError(e)

    def search_fingerprint(self, timeout=10):
        """Search for the fingerprint"""
        try:
            logging.info("Place finger on sensor")

            start_time = time.time()

            while not self.f.readImage():
                if time.time() - start_time > timeout:
                    logging.info("Timeout reached")
                    return None
                pass

            self.f.convertImage()
            result = self.f.searchTemplate()

            if result[0] == -1:
                logging.info("No match found try again.")
                return None
            else:
                logging.info("found fingerprint " + str(result[0]))
                return result[0]

        except Exception as e:
            logging.info("error detecting fingerprint %s", e)
            return None

    def clear_fingerprints(self):
        """Clear all fingerprints stored in the database."""
        try:
            self.f.clearDatabase()
            logging.info("All fingerprints cleared successfully.")
        except Exception as e:
            logging.error("Failed to clear fingerprints: %s", e)
            raise FingerprintError(e)
