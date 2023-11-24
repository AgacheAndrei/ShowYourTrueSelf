package org.faciee;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PythonIntegration {
    public static void main(String[] args) {
        // Define the Python script paths
        String script1 = "src/main/python/org/faciee/workers/test1.py";
        String script2 = "src/main/python/org/faciee/workers/test2.py";

        // Create and start threads to execute the Python scripts
        Thread thread1 = new Thread(() -> executePythonScript(script1));
        Thread thread2 = new Thread(() -> executePythonScript(script2));

        thread1.start();
        thread2.start();

        // Wait for threads to finish
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Java application finished.");
    }

    private static void executePythonScript(String scriptPath) {
        try {
            // Provide the full path to the Python executable
            String pythonExecutable = "Python/python.exe";

            ProcessBuilder processBuilder = new ProcessBuilder(pythonExecutable, scriptPath);
            processBuilder.redirectErrorStream(true);

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

