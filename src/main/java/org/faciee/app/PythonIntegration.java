package org.faciee.app;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(basePackages = {"org.faciee"})
public class PythonIntegration {
    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(PythonIntegration.class, args);
        PythonIntegrationLogic pythonIntegrationLogic = context.getBean(PythonIntegrationLogic.class);
        pythonIntegrationLogic.run();
    }
}
