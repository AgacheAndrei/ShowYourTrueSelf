package org.faciee.app;

import lombok.extern.slf4j.Slf4j;
import org.faciee.service.python.PythonScriptExecutor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PythonIntegrationLogic {

    private final PythonScriptExecutor pythonScriptExecutor;

    public PythonIntegrationLogic(PythonScriptExecutor pythonScriptExecutor) {
        this.pythonScriptExecutor = pythonScriptExecutor;
    }


    public void run() {
        pythonScriptExecutor.execute("PythonRunner/src/main/python/org/faciee/workers/blurTest.py", "param1", "param2");
    }
}
