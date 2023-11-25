package org.faciee.gui;

import lombok.extern.slf4j.Slf4j;
import org.faciee.service.queue.VideoQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class AppGUI {

    @Autowired
    private VideoQueue videoQueue;

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
    public String uploadFileNames(@RequestBody List<String> fileNames) {
        try {
            // Handle the received file names (e.g., add them to the queue)
            videoQueue.addVideos(fileNames);

            log.info("Received file names: {}", fileNames);

            return "File names uploaded successfully.";
        } catch (Exception e) {
            log.error("Error processing file names upload", e);
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
