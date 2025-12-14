import board
import busio
import adafruit_ssd1305
import logging

from PIL import Image, ImageDraw

i2c = busio.I2C(board.SCL, board.SDA)
display = adafruit_ssd1305.SSD1305_I2C(128, 32, i2c)

logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    datefmt='%Y-%m-%d %H:%M:%S',
)

def draw(string_list):
    """
    Draws a list of strings on the display.

    Args:
        string_list (list): A list of strings to be displayed.

    Returns:
        None
    """
    index = 0
    image = Image.new('1', (128, 32))
    drawer = ImageDraw.Draw(image)
    for item in string_list:
        drawer.text((0, index), str(item).upper(), fill="white")
        index += 10
    display.image(image)
    display.show()
    logging.info("Display updated with %s", string_list)

if __name__ == "__main__":
    draw(["Hello", "World"])