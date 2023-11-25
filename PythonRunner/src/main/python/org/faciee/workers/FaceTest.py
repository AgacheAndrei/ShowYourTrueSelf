import time
from typing import Tuple, Union
import math
import cv2
import numpy as np
import mediapipe as mp

MARGIN = 10  # pixels
ROW_SIZE = 10  # pixels
FONT_SIZE = 1
FONT_THICKNESS = 1
TEXT_COLOR = (255, 0, 0)  # red

def _normalized_to_pixel_coordinates(normalized_x, normalized_y, image_width, image_height):
    def is_valid_normalized_value(value):
        return (value > 0 or math.isclose(0, value)) and (value < 1 or math.isclose(1, value))

    if not (is_valid_normalized_value(normalized_x) and is_valid_normalized_value(normalized_y)):
        return None
    x_px = min(math.floor(normalized_x * image_width), image_width - 1)
    y_px = min(math.floor(normalized_y * image_height), image_height - 1)
    return x_px, y_px


def eye_aspect_ratio(eye_landmarks):
    # Extract (x, y) coordinates from the landmarks
    eye_landmarks = np.array(eye_landmarks)

    # Compute the Euclidean distances between the two sets of vertical eye landmarks
    A = np.linalg.norm(eye_landmarks[1] - eye_landmarks[5])
    B = np.linalg.norm(eye_landmarks[2] - eye_landmarks[4])

    # Compute the Euclidean distance between the horizontal eye landmarks
    C = np.linalg.norm(eye_landmarks[0] - eye_landmarks[3])

    # Compute the eye aspect ratio
    ear = (A + B) / (2 * C)

    return ear

def eye_closed(landmarks):
    if len(landmarks) < 12:
        return True

    # Extract landmarks for the left eye
    left_eye_landmarks = [(landmarks[i].x, landmarks[i].y) for i in range(159, 145, -1)]  # Left eye landmarks

    # Compute eye aspect ratio
    ear_threshold = 0.2  # Adjust this threshold as needed
    left_ear = eye_aspect_ratio(left_eye_landmarks)

    # Return True if the left eye is closed
    return left_ear < ear_threshold

def visualize(image, detection_result, face_landmarks_result):
    annotated_image = image.copy()
    height, width, _ = image.shape

    # Initialize face counter
    face_counter = 0

    for detection, face_landmarks in zip(detection_result.detections, face_landmarks_result.multi_face_landmarks):
        bboxC = detection.location_data.relative_bounding_box
        ih, iw, _ = image.shape
        bbox = int(bboxC.xmin * iw), int(bboxC.ymin * ih), \
            int(bboxC.width * iw), int(bboxC.height * ih)

        start_point = bbox[0], bbox[1]
        end_point = bbox[0] + bbox[2], bbox[1] + bbox[3]

        # Get landmarks
        landmarks = face_landmarks.landmark

        # Check if the left eye is closed
        if eye_closed(landmarks):
            print("Eyes Closed!")
            continue  # Skip this frame if the left eye is closed

        # Draw rectangles and circles
        cv2.rectangle(annotated_image, start_point, end_point, TEXT_COLOR, 3)

        for landmark in landmarks:
            landmark_px = _normalized_to_pixel_coordinates(landmark.x, landmark.y, width, height)
            color, thickness, radius = (0, 255, 0), 2, 2
            cv2.circle(annotated_image, landmark_px, thickness, color, radius)

        # Updated attribute names
        label = f"{int(detection.score[0] * 100)}%"  # Convert score to percentage
        text_location = (MARGIN + start_point[0], MARGIN + ROW_SIZE + start_point[1])
        cv2.putText(annotated_image, label, text_location, cv2.FONT_HERSHEY_PLAIN,
                    FONT_SIZE, TEXT_COLOR, FONT_THICKNESS)

        # Increment face counter
        face_counter += 1

    return annotated_image, face_counter
    # Draw face landmarks
    face_landmarks_list = face_landmarks_result.face_landmarks
    for idx in range(len(face_landmarks_list)):
        face_landmarks = face_landmarks_list[idx]

        # Draw the face landmarks.
        face_landmarks_proto = mp.solutions.face_mesh.Facemesh.FromString(face_landmarks)
        mp.solutions.drawing_utils.draw_landmarks(
            image=annotated_image,
            landmark_list=face_landmarks_proto,
            connections=mp.solutions.face_mesh.FACEMESH_TESSELATION,
            landmark_drawing_spec=None,
            connection_drawing_spec=mp.solutions.drawing_styles.get_default_face_mesh_tesselation_style())
        mp.solutions.drawing_utils.draw_landmarks(
            image=annotated_image,
            landmark_list=face_landmarks_proto,
            connections=mp.solutions.face_mesh.FACEMESH_CONTOURS,
            landmark_drawing_spec=None,
            connection_drawing_spec=mp.solutions.drawing_styles.get_default_face_mesh_contours_style())
        mp.solutions.drawing_utils.draw_landmarks(
            image=annotated_image,
            landmark_list=face_landmarks_proto,
            connections=mp.solutions.face_mesh.FACEMESH_IRISES,
            landmark_drawing_spec=None,
            connection_drawing_spec=mp.solutions.drawing_styles.get_default_face_mesh_iris_connections_style())

    return annotated_image, face_counter

# Update the model path
detector = mp.solutions.face_detection.FaceDetection(model_selection=1)
face_mesh = mp.solutions.face_mesh.FaceMesh(static_image_mode=False, max_num_faces=2, min_detection_confidence=0.1, min_tracking_confidence=0.1)



# Open a video capture from a video file or the PC's camera
video_path = "D:/Concurs Unguri/soagaaaa.mp4"  # Update this with the correct path
video_capture = cv2.VideoCapture(video_path) if cv2.VideoCapture(video_path).isOpened() else cv2.VideoCapture(0)

# Initialize the start time
start_time = time.time()
while True:
    ret, frame = video_capture.read()

    if not ret:
        break

    rgb_frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)

    detections = detector.process(rgb_frame)

    rgb_frame_mesh = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
    mp_image = mp.Image(image_format=mp.ImageFormat.SRGB, data=rgb_frame_mesh)
    image_np = np.array(mp_image.numpy_view())

    face_landmarks_result = face_mesh.process(image_np)

    annotated_frame, face_counter = visualize(frame, detections, face_landmarks_result)


    # Display the annotated frame using cv2.imshow
    cv2.imshow('Combined Detection', annotated_frame)

    if face_counter > 1:
        print("More than one face detected! Closing the video.")
        break

    if eye_closed(face_landmarks_result.multi_face_landmarks[0].landmark):  # Assuming the first detected face
        elapsed_time = time.time() - start_time
        if elapsed_time > 3:
            print("Eyes closed for more than 3 seconds! Closing the video.")
            break
    else:
        start_time = time.time()

    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# Release the video capture and close all windows
video_capture.release()
cv2.destroyAllWindows()