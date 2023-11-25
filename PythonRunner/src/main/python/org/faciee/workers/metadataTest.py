import sys

from moviepy.editor import VideoFileClip
from datetime import datetime
import os


if len(sys.argv) > 1:
    param1 = sys.argv[1]
    video_path = param1

def get_video_metadata(video_path):
    metadata = {}

    try:
        with VideoFileClip(video_path) as video:
            # Basic properties
            metadata['duration'] = video.duration
            metadata['size'] = video.size
            metadata['fps'] = video.fps

            # Creation time from file creation timestamp
            created_timestamp = os.path.getctime(video_path)
            metadata['creation_time'] = datetime.fromtimestamp(created_timestamp)

            # Video resolution
            metadata['resolution'] = video.size

    except Exception as e:
        print(f"Fail : Error reading metadata: {e}",param1)

    return metadata

def check_metadata(video_path):
    metadata = get_video_metadata(video_path)

    # Print metadata information
    print("Metadata:")
    for key, value in metadata.items():
        print(f"{key}: {value}")

    # Verify metadata
    if 'creation_time' in metadata:
        current_time = datetime.now()
        time_difference = current_time - metadata['creation_time']
        if time_difference.days > 30:
            print("Fail : The video is older than 30 days. Verify the creation time.",param1)
        else:
            print("Success : The video seems recent.",param1)
    else:
        print("Creation time not found in metadata.")

check_metadata(video_path)