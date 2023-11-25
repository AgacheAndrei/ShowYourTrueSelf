package org.faciee.service.queue;

import lombok.ToString;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
@ToString
public class VideoQueueImpl implements VideoQueue {
    private final Queue<String> videoQueue;

    public VideoQueueImpl() {
        this.videoQueue = new LinkedBlockingQueue<>();
    }

    public void addVideo(String videoPath) {
        videoQueue.add(videoPath);
    }

    public Queue<String> getVideos() {
        return videoQueue;
    }

    public String popVideo() {
        return videoQueue.poll();
    }

    public String peekVideo() {
        return videoQueue.peek();
    }

    @Override
    public void addVideos(List<String> fileNames) {
        videoQueue.addAll(fileNames);
    }

    @Override
    public boolean isQEmpty() {
        return videoQueue.isEmpty();
    }
}
