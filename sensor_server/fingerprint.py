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

    def capture_finger(self, timeout=10):
        """
        Capture a fingerprint and store it if its unique

        Args:
            timeout (int): Timeout in seconds for fingerprint capture

        Returns:
            int: Position number of the captured fingerprint

        Raises:
            FingerprintError: If fingerprint capture fails or timeout is reached
        """
        logging.info("Capturing fingerprint. Stored fingerprints %d", self.f.getTemplateCount())

        start_time = time.time()
        try:
            logging.info("Please place your finger on the sensor.")
            draw(["Place your", "finger"])
            while not self.f.readImage():
                time.sleep(0.1)
                if time.time() - start_time > timeout:
                    raise FingerprintError("Enroll timeout reached")
                pass

            self.f.convertImage(0x01)
            result = self.f.searchTemplate()
            position_number = result[0]

            if position_number >= 0:
                logging.warning("Fingerprint already exists at position #%d.", position_number + 1)
                draw(["Fingerprint", "already exists"])
                raise FingerprintError("Fingerprint already exists")

            logging.info("Remove your finger.")
            draw(["Remove your", "finger"])
            while self.f.readImage():
                time.sleep(0.1)
                if time.time() - start_time > timeout:
                    raise FingerprintError("Enroll timeout reached")
                pass

            logging.info("Place your finger again.")
            draw(["Place your", "finger again"])
            while not self.f.readImage():
                time.sleep(0.1)
                if time.time() - start_time > timeout:
                    raise FingerprintError("Enroll timeout reached")
                pass

            self.f.convertImage(0x02)

            if self.f.compareCharacteristics() == 0:
                logging.warning("Fingerprints do not match.")
                draw(["Fingerprints", "do not match"])
                raise FingerprintError("Fingerprints do not match")

            self.f.createTemplate()
            position = self.f.storeTemplate()
            logging.info("Fingerprint stored successfully at position #%d.", position)
            draw(["Fingerprint", "stored successfully"])
            return position

        except Exception as e:
            logging.error("Error during fingerprint capture: %s", e)
            raise FingerprintError(e)

    def delete_fingerprint(self, index):
        """Delete a specific fingerprint"""
        try:
            self.f.deleteTemplate(index)
            logging.info(f"Deleted template at: {index}")
        except Exception as e:
            logging.error("Error deleting template")
            raise FingerprintError(e)

    def search_fingerprint(self, timeout=1):
        """
        Search for the fingerprint

        Args:
            timeout (int): Timeout in seconds for fingerprint capture

        Returns:
            int: Position number of the captured fingerprint

        Raises:
            FingerprintError: If fingerprint capture fails or timeout is reached
        """
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

if __name__ == "__main__":
    fingerprint = FingerprintSensor()

    enroll = fingerprint.capture_finger()
    print(enroll)

    position = fingerprint.search_fingerprint()
    print(position)

    fingerprint.delete_fingerprint(enroll)