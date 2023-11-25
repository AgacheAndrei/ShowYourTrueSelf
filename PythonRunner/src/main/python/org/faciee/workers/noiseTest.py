import sys

import cv2
import numpy as np
import time

def calculate_noise(image):
    # Convertirea imaginii la nivel de gri, dacă nu este deja
    if len(image.shape) > 2:
        gray_image = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    else:
        gray_image = image

    # Calcularea deviației standard
    std_dev = np.std(gray_image)

    return std_dev

if len(sys.argv) > 1:
    param1 = sys.argv[1]
    video_path = param1
    video_reader = cv2.VideoCapture(video_path)


start_time = time.time()
frames_processed = 0
average_noise = 0
while True:

    ret, frame = video_reader.read()

    if not ret:
        break

    frames_processed += 1

    noise_level = calculate_noise(frame)
    average_noise += noise_level


end_time = time.time()


duration = end_time - start_time

if(average_noise / (frames_processed)>80):
    print("Fail : too much noise",param1)
else:
    print("Success : low noise level",param1)


# Eliberarea resurselor
video_reader.release()
cv2.destroyAllWindows()