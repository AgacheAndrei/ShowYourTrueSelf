package org.faciee.app;

import lombok.extern.slf4j.Slf4j;
import org.faciee.gui.AppGUI;
import org.faciee.service.python.PythonScriptExecutor;
import org.faciee.service.queue.VideoQueue;
import org.faciee.service.queue.VideoQueueImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PythonIntegrationLogic {

    @Autowired
    private PythonScriptExecutor pythonScriptExecutor;

    @Autowired
    private AppGUI appGUI;


    public void run() {
        log.info("sunt in run");
        // Example usage
//        pythonScriptExecutor.execute("PythonRunner/src/main/python/org/faciee/workers/test1.py", "param1", "param2");
    }
}
