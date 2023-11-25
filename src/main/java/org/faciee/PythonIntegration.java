package org.faciee;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class PythonIntegration {
    public static void main(String[] args) {
        // Define the Python script paths
        String script1 = "PythonRunner/src/main/python/org/faciee/workers/test1.py";
        String script2 = "src/main/python/org/faciee/workers/test2.py";

        // Define parameters for the Python scripts
        String param1 = "path/to/video.mp4";
        String param2 = "param2_value";

        // Create and start threads to execute the Python scripts
        Thread thread1 = new Thread(() -> executePythonScript(script1, param1));
//        Thread thread2 = new Thread(() -> executePythonScript(script2, param2));

        thread1.start();
//        thread2.start();

        // Wait for threads to finish
        try {
            thread1.join();
//             thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Java application finished.");
    }

    private static void executePythonScript(String scriptPath, String... params) {
        try {
            // Build the command for the Python script with parameters
            ProcessBuilder processBuilder = new ProcessBuilder("Python/python.exe", scriptPath);
            processBuilder.redirectErrorStream(true);

            // Add parameters to the command
            processBuilder.command().addAll(Arrays.asList(params));

            Process process = processBuilder.start();

            // Capture and print the output of the Python script
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            System.out.println("Script exited with code " + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}