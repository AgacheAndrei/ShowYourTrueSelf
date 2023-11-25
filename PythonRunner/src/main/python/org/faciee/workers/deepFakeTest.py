import math
import sys
import time

import cv2
import cvzone
from ultralytics import YOLO

confidence = 0.6

# cap = cv2.VideoCapture(1)  # For Webcam
# cap.set(3, 640)
# cap.set(4, 480)

if len(sys.argv) > 1:
    param1 = sys.argv[1]
    video_path = param1

cap = cv2.VideoCapture(video_path)  # For Video

model = YOLO(r"trainedModel/l_version_1_300/l_version_1_300.pt")

classNames = ["fake", "real"]

prev_frame_time = 0
new_frame_time = 0

while True:
    new_frame_time = time.time()
    success, img = cap.read()
    results = model(img, stream=True, verbose=False)
    for r in results:
        boxes = r.boxes
        for box in boxes:
            conf = math.ceil((box.conf[0] * 100)) / 100
            cls = int(box.cls[0])
            if conf > confidence:
                print("Success : The video is not fake", f'{classNames[cls].upper()}: {int(conf*100)}%')
                break

    fps = 1 / (new_frame_time - prev_frame_time)
    prev_frame_time = new_frame_time
    print(f'FPS: {fps}')

print("Fail : The video is a deepfake")