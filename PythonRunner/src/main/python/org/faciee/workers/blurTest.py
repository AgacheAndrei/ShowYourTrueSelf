import cv2
import numpy as np
import imageio

video_path = r"C:\Users\sogaa\IdeaProjects\ShowYourTrueSelf\videos\WhatsApp Video 2023-11-24 at 23.07.28_3781004c.mp4"
video_reader = imageio.get_reader(video_path)

for frame in video_reader:
    gray_frame = cv2.cvtColor(frame, cv2.COLOR_RGB2GRAY)

    laplacian_var = cv2.Laplacian(gray_frame, cv2.CV_64F).var()

    if laplacian_var < 5:
        print("Cadru estompat")

    print(laplacian_var)

    cv2.imshow("Frame", cv2.cvtColor(frame, cv2.COLOR_RGB2BGR))

    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

cv2.destroyAllWindows()