package org.faciee.service.python;

import java.util.List;

public interface PythonScriptExecutor {
    void execute(String scriptPath, String... params);
    List<String> getSuccessLines();
    List<String> getFailLines();

}
