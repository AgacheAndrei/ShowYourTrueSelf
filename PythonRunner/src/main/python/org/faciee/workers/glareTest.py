import sys
import cv2

def detect_glare(frame, threshold_value=250, glare_threshold=0.01):
    gray_frame = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    _, thresholded = cv2.threshold(gray_frame, threshold_value, 255, cv2.THRESH_BINARY)
    contours, _ = cv2.findContours(thresholded, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

    for contour in contours:
        x, y, w, h = cv2.boundingRect(contour)
        area = cv2.contourArea(contour)
        frame_size = gray_frame.shape[0] * gray_frame.shape[1]

        if area > glare_threshold * frame_size:
            return True  # High glare detected

    return False

if len(sys.argv) > 1:
    param1 = sys.argv[1]
    video_path = param1

cap = cv2.VideoCapture(video_path)

while cap.isOpened():
    ret, frame = cap.read()
    if not ret:
        break

    # Perform glare detection
    if detect_glare(frame):
        print("Fail : High glare detected!",param1)
        break

    if cv2.waitKey(25) & 0xFF == ord('q'):
        break
print("Success : High glare was not detected!",param1)
cap.release()
cv2.destroyAllWindows()
