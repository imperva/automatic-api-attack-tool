package com.imperva.apiattacktool;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import com.imperva.apiattacktool.cli.ApiAttackTool;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.net.URL;


public class ToolMain {
    public static void main(String[] args) {
        configureLogging();
        ApiAttackTool apiAttackTool = new ApiAttackTool();
        System.exit(new CommandLine(apiAttackTool).execute(args));
    }

    private static void configureLogging() {
        final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        try {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext( context );
            context.reset();
            URL path = ClassLoader.getSystemClassLoader().getResource("logback.xml");
            configurator.doConfigure(path);
        }
        catch ( JoranException ignore ) {
            System.out.println("Logging is disabled");
        }
    }
}
