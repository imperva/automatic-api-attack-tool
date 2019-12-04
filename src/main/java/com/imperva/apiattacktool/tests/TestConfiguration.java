package com.imperva.apiattacktool.tests;

import com.imperva.apiattacktool.cli.ApiAttackTool;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestConfiguration {

    private static final int DEFAULT_PROXY_PORT = 80;
    private static final int DEFAULT_HOST_PORT = 443;

    private static String specFilePath = System.getProperty("specFile", null);

    private static String hostScheme = System.getProperty("hostScheme", null);

    private static String hostName = System.getProperty("hostName", null);

    private static Integer hostPort = getIntegerFieldFromProperty("hostPort", DEFAULT_HOST_PORT);

    private static String proxyHost = System.getProperty("proxyHost", null);

    private static Integer proxyPort = getIntegerFieldFromProperty("proxyPort", DEFAULT_PROXY_PORT);

    private static Collection<Integer> userProvidedPositiveResponseCodes = getIntegerListFromProperty("addPositiveRC", Collections.emptyList());

    private static Collection<Integer> userProvidedNegativeResponseCodes = getIntegerListFromProperty("addNegativeRC", Collections.emptyList());

    public static void initFrom(ApiAttackTool apiAttackToolOptions) {
        specFilePath = apiAttackToolOptions.getSpecFilePath();
        hostScheme = apiAttackToolOptions.getHostScheme();
        hostName = apiAttackToolOptions.getHostName();
        hostPort = apiAttackToolOptions.getHostPort();
        proxyHost = apiAttackToolOptions.getProxyHost();
        proxyPort = apiAttackToolOptions.getProxyPort();
        userProvidedPositiveResponseCodes =
            apiAttackToolOptions.getUserProvidedPositiveResponseCodes() == null
                ? Collections.emptyList()
                : new HashSet<>(apiAttackToolOptions.getUserProvidedPositiveResponseCodes());
        userProvidedNegativeResponseCodes =
            apiAttackToolOptions.getUserProvidedNegativeResponseCodes() == null
                ? Collections.emptyList()
                : new HashSet<>(apiAttackToolOptions.getUserProvidedNegativeResponseCodes());
    }

    public static String getSpecFilePath() {
        return specFilePath;
    }

    public static String getHostScheme() {
        return hostScheme;
    }

    public static String getHostName() {
        return hostName;
    }

    public static int getHostPort() {
        return hostPort == null ? DEFAULT_HOST_PORT : hostPort;
    }

    public static String getProxyHost() {
        return proxyHost;
    }

    public static int getProxyPort() {
        return proxyPort == null ? DEFAULT_PROXY_PORT : proxyPort;
    }

    public static boolean isProxyDefined() {
        return !StringUtils.isBlank(proxyHost);
    }

    public static Collection<Integer> getUserProvidedPositiveResponseCodes() {
        return userProvidedPositiveResponseCodes;
    }

    public static Collection<Integer> getUserProvidedNegativeResponseCodes() {
        return userProvidedNegativeResponseCodes;
    }

    public static String getWorkingConfigurationString() {
        return "API Spec file path: " + specFilePath + "\n"
        + "Host: (" + hostScheme + ") " + hostName + " : " + getHostPort() + "\n"
        + (isProxyDefined()
            ? "Proxy Host: " + proxyHost + " : " + getProxyPort() + "\n"
            : "");

    }

    private static Integer getIntegerFieldFromProperty(String propertyName, Integer defaultValue) {
        String propertyValue = System.getProperty(propertyName);
        if (StringUtils.isBlank(propertyValue)) {
            return defaultValue;
        }

        return Integer.parseInt(propertyValue);
    }

    private static Collection<Integer> getIntegerListFromProperty(String propertyName, List<Integer> defaultValue) {
        String propertyValue = System.getProperty(propertyName);
        if (StringUtils.isBlank(propertyValue)) {
            return defaultValue;
        }

        String[] listValues = propertyValue.split("[,]");
        Collection<Integer> integerCollection =
            Stream.of(listValues).map(Integer::parseInt).collect(Collectors.toCollection(HashSet::new));
        return integerCollection;
    }
}
