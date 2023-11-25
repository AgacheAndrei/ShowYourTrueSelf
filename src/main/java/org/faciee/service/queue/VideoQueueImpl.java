package org.faciee.service.queue;

import lombok.ToString;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Service
@ToString
public class VideoQueueImpl implements VideoQueue {
    private final Queue<String> videoQueue;

    public VideoQueueImpl() {
        this.videoQueue = new LinkedList<>();
    }

    public void addVideo(String videoPath) {
        videoQueue.add(videoPath);
    }

    public Queue<String> getVideos() {
        return videoQueue;
    }

    public String popVideo() {
        if (!videoQueue.isEmpty()) {
            return videoQueue.poll();
        } else {
            return null;
        }
    }

    public String peekVideo() {
        return videoQueue.peek();
    }

    @Override
    public void addVideos(List<String> fileNames) {
        videoQueue.addAll(fileNames);
    }
}
