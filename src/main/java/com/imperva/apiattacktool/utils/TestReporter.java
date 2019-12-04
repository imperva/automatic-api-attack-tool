package com.imperva.apiattacktool.utils;

import org.testng.Reporter;

public class TestReporter {

    public static void log(Object message) {
        log(String.valueOf(message));
    }

    public static void log(String message) {
        Reporter.log(message, true);
    }

    public static void log(Exception exception) {
        Throwable currentException = exception;
        int i = 0;
        do {
            Reporter.log((i > 0 ? "at " : "") + currentException.getMessage(), true);
            currentException = currentException.getCause();
            i++;
        } while (currentException != null);
    }
}
