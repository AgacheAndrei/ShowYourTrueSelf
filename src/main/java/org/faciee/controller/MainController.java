package org.faciee.controller;

import lombok.extern.slf4j.Slf4j;
import org.faciee.service.queue.VideoQueue;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class MainController {

    private final VideoQueue videoQueue;

    public MainController(VideoQueue videoQueue) {
        this.videoQueue = videoQueue;
    }

    @GetMapping("/hello")
    public String sayHello(Model model) {
        model.addAttribute("message", "Hello, Thymeleaf!");
        return "hello"; // This corresponds to the Thymeleaf template named "hello.html"
    }

    @GetMapping("/fileChooser")
    public String showFileChooser() {
        return "fileChooser";
    }

    @PostMapping("/uploadFileNames")
    @ResponseBody
    public String uploadFileNames(@RequestParam("files") List<MultipartFile> files) {
        try {
            // Ensure the "videos" directory exists
            String videosDirectoryPath = "videos";
            File videosDirectory = new File(videosDirectoryPath);
            if (!videosDirectory.exists()) {
                videosDirectory.mkdirs(); // Create the directory if it doesn't exist
            }

            for (MultipartFile file : files) {
                String fileName = file.getOriginalFilename();
                Path filePath = Paths.get(videosDirectoryPath, fileName);

                // Check if the file with the same name already exists
                if (Files.exists(filePath)) {
                    Files.delete(filePath); // Delete the existing file
                }

                // Save the new file to the "videos" directory
                Files.copy(file.getInputStream(), filePath);
            }

            // Add the file paths to the queue
            List<String> filePaths = files.stream()
                    .map(file -> Paths.get(videosDirectoryPath, file.getOriginalFilename()).toString())
                    .collect(Collectors.toList());
            videoQueue.addVideos(filePaths);

            log.info("Uploaded files to: {}", videosDirectoryPath);

            return "File names uploaded successfully.";
        } catch (IOException e) {
            log.error("Error processing file names upload: {}", e.getMessage());
            return "Error processing file names upload.";
        }
    }


    @GetMapping("/displayQueue")
    public String displayQueue() {
        log.info("File Queue: {}", videoQueue.toString());
        return "redirect:/fileChooser";
    }

    @GetMapping("/getQueue")
    @ResponseBody
    public List<String> getQueue() {
        return new ArrayList<>(videoQueue.getVideos());
    }
}
