package org.faciee.service.python;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

@Service
@Slf4j
public class PythonScriptExecutorImpl implements PythonScriptExecutor {

    @Override
    public void execute(String scriptPath, String... params) {
        try {
            // Build the command for the Python script with parameters
            ProcessBuilder processBuilder = new ProcessBuilder("PythonRunner/venv/Scripts/python.exe", scriptPath);
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
            log.error(e.getMessage());
        }
    }
}
