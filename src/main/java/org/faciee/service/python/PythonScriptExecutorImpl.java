package org.faciee.service.python;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class PythonScriptExecutorImpl implements PythonScriptExecutor {

    @Value("${pythonInterpreter}")
    String python;

    @Getter
    private List<String> successLines = new ArrayList<>();
    @Getter
    private List<String> failLines = new ArrayList<>();

    @Override
    public void execute(String scriptPath, String... params) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(python, scriptPath);
            processBuilder.redirectErrorStream(true);

            processBuilder.command().addAll(Arrays.asList(params));

            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Fail")) {
                    failLines.add(line);
                    System.out.println(line);
                }

                if (line.contains("Success")) {
                    successLines.add(line);
                    System.out.println(line);
                }
            }

            int exitCode = process.waitFor();

        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage());
        }
    }

}
