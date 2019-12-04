package com.imperva.apispecparser.utils;

import com.imperva.apiattacktool.utils.TestReporter;
import com.imperva.apispecparser.parsers.ApiSpecFileLocation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class FileUtils {

    public static String readFile(String filePath, ApiSpecFileLocation apiSpecFileLocation) {
        if (filePath == null) {
            return null;
        }

        String fileContent = null;

        try {
            fileContent = apiSpecFileLocation == ApiSpecFileLocation.EXTERNAL ? readFile(filePath) : readResource(filePath);
        } catch (IOException e) {
            TestReporter.log("Failed to read file [" + filePath + "]");
        }

        return fileContent;
    }

    public static String readFile(String filePath) throws IOException {
        File file = new File(filePath);
        return org.apache.commons.io.FileUtils.readFileToString(file, StandardCharsets.UTF_8);
    }

    public static String readResource(String resourcePath) {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        InputStream is = classLoader.getResourceAsStream(resourcePath);
        if (is != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
        return null;
    }
}
