import RPi.GPIO as GPIO

# GPIO pin mappings
LINES = [5, 6, 13, 19]
COLS = [12, 16, 20, 21]

KEYS = [
    ["1", "2", "3", "A"],
    ["4", "5", "6", "B"],
    ["7", "8", "9", "C"],
    ["*", "0", "#", "D"]
]

        # Setup GPIO
GPIO.setwarnings(False)
GPIO.setmode(GPIO.BCM)

for line in LINES:
    GPIO.setup(line, GPIO.OUT)

for col in COLS:
    GPIO.setup(col, GPIO.IN, pull_up_down=GPIO.PUD_DOWN)

def read_key():
    """Reads a single key press (if any)"""
    for i, line in enumerate(LINES):
        GPIO.output(line, GPIO.HIGH)
        for j, col in enumerate(COLS):
            if GPIO.input(col) == GPIO.HIGH:
                GPIO.output(line, GPIO.LOW)
                return KEYS[i][j]
        GPIO.output(line, GPIO.LOW)
    return None

if __name__ == "__main__":
    while True:
        key = read_key()
        if key is not None:
            print(key)