package org.faciee.controller;

import lombok.extern.slf4j.Slf4j;
import org.faciee.service.python.PythonScriptExecutor;
import org.faciee.service.queue.VideoQueue;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class MainController {

    private final VideoQueue videoQueue;
    private final PythonScriptExecutor pythonScriptExecutor;

    public MainController(VideoQueue videoQueue,PythonScriptExecutor pythonScriptExecutor) {
        this.videoQueue = videoQueue;
        this.pythonScriptExecutor = pythonScriptExecutor;
    }

    @GetMapping("/")
    public String sayHello() {
        return "home";
    }

    @GetMapping("/fileChooser")
    public String showFileChooser(Model model) {
        // Initialize the uploadedFileNames attribute to an empty list
        model.addAttribute("uploadedFileNames", List.of());
        return "fileChooser";
    }

    @PostMapping("/uploadFileNames")
    public String uploadFileNames(@RequestParam("files") List<MultipartFile> files, Model model) {
        try {
            String videosDirectoryPath = "videos";
            File videosDirectory = new File(videosDirectoryPath);
            if (!videosDirectory.exists()) {
                videosDirectory.mkdirs();
            }

            for (MultipartFile file : files) {
                String fileName = file.getOriginalFilename();
                Path filePath = Paths.get(videosDirectoryPath, fileName);

                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                }

                Files.copy(file.getInputStream(), filePath);
            }

            List<String> filePaths = files.stream()
                    .map(file -> Paths.get(videosDirectoryPath, file.getOriginalFilename()).toString())
                    .collect(Collectors.toList());
            videoQueue.addVideos(filePaths);

            log.info("Uploaded files to: {}", videosDirectoryPath);

            // Add the processed file names to the model
            model.addAttribute("uploadedFileNames", filePaths);

            return "fileChooser";
        } catch (IOException e) {
            log.error("Error processing file names upload: {}", e.getMessage());
            return "Error processing file names upload.";
        }

    }
    @GetMapping("/displayResults")
    public String displayResults(Model model) {
        model.addAttribute("successLines", pythonScriptExecutor.getSuccessLines());
        model.addAttribute("failLines", pythonScriptExecutor.getFailLines());
        return "displayResults";
    }
}
