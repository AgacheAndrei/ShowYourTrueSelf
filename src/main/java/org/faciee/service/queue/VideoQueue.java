package org.faciee.service.queue;

import java.util.List;
import java.util.Queue;

public interface VideoQueue {
    void addVideo(String path);

    Queue<String> getVideos();

    String popVideo();

    String peekVideo();

    void addVideos(List<String> fileNames);
}
