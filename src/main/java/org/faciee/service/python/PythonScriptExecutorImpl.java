package org.faciee.service.python;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

@Service
public class PythonScriptExecutorImpl implements PythonScriptExecutor {

    @Override
    public void execute(String scriptPath, String... params) {
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
