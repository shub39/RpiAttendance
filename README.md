![icon](icon.png)

# RpiAttendance

Kotlin/Native server to manage attendance on a raspberry pi 4B
from a python server managing biometric sensors and camera.

## Requirements
- Raspberry pi (Used in this project is 4B 8gb variant)
- R307 fingerprint scanner with UART to USB Converter
- ssd1305 OLED Display 
- Raspberry pi camera
- 4x4 matrix membrane keypad
- Jumper cables and power adapter

## Setup
Required OS for Raspberry pi: `Debian GNU/Linux 12 (bookworm) aarch64`

### Setup Sensor Server
- Clone the repo at the home directory
```shell
git clone https://github.com/shub39/RpiAttendance
```
- Install [uv package manager](https://docs.astral.sh/uv/getting-started/installation/)
- Setup the server environment
```shell
# Setup
cd RpiAttandance/sensor_server/
uv venv
uv pip install

sudo apt update
sudo apt upgrade
sudo apt install pip python3-opencv python3-numpy cmake
sudo apt install libkms++-dev libfmt-dev libdrm-dev # might require more...

uv pip install rpi-libcamera -C setup-args="-Dversion=unknown"
uv pip install -r requirements.txt
```
- Setup the cronjob to run server on reboot, file `cronjob.sh`

### Setup Server Binary
- Download the binary from [releases](https://github.com/shub39/RpiAttendance/releases) to the home directory
- Setup cronjob
- Install android app on a device in the same network as the Raspberry Pi

