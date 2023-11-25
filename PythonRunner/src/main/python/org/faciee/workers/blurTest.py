import sys
from time import time
import cv2
import cvzone
from cvzone.FaceDetectionModule import FaceDetector


classID = 0
confidence = 0.8
blurThreshold = 35

debug = False
offsetPercentageW = 10
offsetPercentageH = 20
camWidth, camHeight = 640, 480
floatingPoint = 6

if len(sys.argv) > 1:
    param1 = sys.argv[1]
    video_path = param1

cap = cv2.VideoCapture(video_path)  # For Video

detector = FaceDetector()
terminate_video = False

while not terminate_video:
    success, img = cap.read()

    if not success:
        print("Fail : Video ended or unable to read the frame.",param1)
        break

    imgOut = img.copy()
    img, bboxs = detector.findFaces(img, draw=False)

    if bboxs:
        for bbox in bboxs:
            x, y, w, h = bbox["bbox"]
            score = bbox["score"][0]

            if score > confidence:

                offsetW = (offsetPercentageW / 100) * w
                x = int(x - offsetW)
                w = int(w + offsetW * 2)
                offsetH = (offsetPercentageH / 100) * h
                y = int(y - offsetH * 3)
                h = int(h + offsetH * 3.5)

                if x < 0: x = 0
                if y < 0: y = 0
                if w < 0: w = 0
                if h < 0: h = 0

                imgFace = img[y:y + h, x:x + w]
                blurValue = int(cv2.Laplacian(imgFace, cv2.CV_64F).var())

                ih, iw, _ = img.shape
                xc, yc = x + w / 2, y + h / 2
                xcn, ycn = round(xc / iw, floatingPoint), round(yc / ih, floatingPoint)
                wn, hn = round(w / iw, floatingPoint), round(h / ih, floatingPoint)

                if xcn > 1: xcn = 1
                if ycn > 1: ycn = 1
                if wn > 1: wn = 1
                if hn > 1: hn = 1

                maxBlur = 1500
                blurPercentage = ((maxBlur - blurValue) / maxBlur) * 100

                cv2.rectangle(imgOut, (x, y, w, h), (255, 0, 0), 3)
                cvzone.putTextRect(imgOut, f'Clarity: {blurValue}%', (x, y - 0), scale=2, thickness=3)
                if debug:
                    cv2.rectangle(img, (x, y, w, h), (255, 0, 0), 3)
                    cvzone.putTextRect(img, f'Clarity: {blurValue}%', (x, y - 0), scale=2, thickness=3)

                if blurValue < 70:
                    print("Fail : Video is fake",param1)
                    terminate_video = True

    # cv2.imshow("Image", imgOut)

    # delay can be modified
    cv2.waitKey(60)