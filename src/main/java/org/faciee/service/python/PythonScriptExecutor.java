package org.faciee.service.python;

public interface PythonScriptExecutor {
    void execute(String scriptPath, String... params);
}
