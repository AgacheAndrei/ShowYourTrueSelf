package org.faciee.app;

import lombok.extern.slf4j.Slf4j;
import org.faciee.service.python.PythonScriptExecutor;
import org.faciee.service.queue.VideoQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component
@Slf4j
public class PythonIntegrationLogic {

    private final ExecutorService executorService;
    private final Map<String, Boolean> videoPathStatusMap; // Map to store the status of each video path
    private final PythonScriptExecutor pythonScriptExecutor;
    private final VideoQueue videoQueue;

    @Value("${blurTest}")
    private String blurTest;
    @Value("${faceTest}")
    private String faceTest;
    @Value("${noiseTest}")
    private String noiseTest;
    @Value("${metadataTest}")
    private String metadataTest;
    @Value("${deepFakeTest}")
    private String deepFakeTest;

    public PythonIntegrationLogic(PythonScriptExecutor pythonScriptExecutor, VideoQueue videoQueue) {
        int numberOfThreads = Runtime.getRuntime().availableProcessors();
        executorService = Executors.newFixedThreadPool(numberOfThreads);
        videoPathStatusMap = new ConcurrentHashMap<>();
        this.pythonScriptExecutor = pythonScriptExecutor;
        this.videoQueue = videoQueue;
    }

    public void run() {
        while (true) {
            while (!videoQueue.isQEmpty()) {
                String videoPath = videoQueue.popVideo();

                if (videoPathStatusMap.containsKey(videoPath) && !videoPathStatusMap.get(videoPath)) {
                    continue;
                }

                Future<?> newTask = executorService.submit(() -> {
                    try {
                        pythonScriptExecutor.execute(blurTest, videoPath);
                        pythonScriptExecutor.execute(faceTest, videoPath);
                        pythonScriptExecutor.execute(noiseTest, videoPath);
                        pythonScriptExecutor.execute(metadataTest, videoPath);
                        pythonScriptExecutor.execute(deepFakeTest, videoPath);
                        if (!pythonScriptExecutor.getFailLines().isEmpty()) {
                            videoPathStatusMap.put(videoPath, false);
                        }
                    } catch (Exception e) {
                        log.error("Error processing video path: {}", videoPath, e);
                        videoPathStatusMap.put(videoPath, false);
                    }
                });
            }
        }
    }
}
