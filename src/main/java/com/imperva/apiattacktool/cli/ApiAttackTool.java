package com.imperva.apiattacktool.cli;

import com.imperva.apiattacktool.tests.TestConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.TestNG;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "api-attack", mixinStandardHelpOptions = true, requiredOptionMarker = '*')
public class ApiAttackTool implements Callable<Integer> {

    private static final Logger logger = LoggerFactory.getLogger(ApiAttackTool.class);

    @Option(names = {"-f", "--specFile"}, required = true, description = "The API specification file (Swagger 2.0) to run on. JSON/YAML format. For better results, make sure responses are well defined for each endpoint.")
    private String specFilePath;

    @Option(names = {"-s", "--hostScheme"}, required = true, description = "Connection to host will be made using this scheme; e.g: https or http")
    private String hostScheme;

    @Option(names = {"-n", "--hostName"}, required = true, description = "The host name to connect to. It can also be an IP")
    private String hostName;

    @Option(names = {"-p", "--hostPort"}, description = "The port the host is listening on for API calls")
    private Integer hostPort;

    @Option(names = {"-ph", "--proxyHost"}, description = "Specify the proxy host to send the requests via a proxy")
    private String proxyHost;

    @Option(names = {"-rpp", "--requestsPerParameter"}, description = "Specify the number of requests to send per the swagger's parameters (Strings excluded)")
    private Integer numOfRequestsPerParameter;

    @Option(names = {"-pp", "--proxyPort"}, description = "The proxy port")
    private Integer proxyPort;

    @Option(names = {"-rcp", "--addPositiveRC"}, split = ",", description = "Additional response codes to be accepted in positive checks (legitimate value attacks). Multiple values are supported, separated by commas")
    private List<Integer> userProvidedPositiveResponseCodes;

    @Option(names = {"-rcn", "--addNegativeRC"}, split = ",", description = "Additional response codes to be accepted in negative attacks (e.g. bad value attacks). Multiple values are supported, separated by commas")
    private List<Integer> userProvidedNegativeResponseCodes;

    public String getSpecFilePath() {
        return specFilePath;
    }

    public String getHostScheme() {
        return hostScheme;
    }

    public String getHostName() {
        return hostName;
    }

    public Integer getHostPort() {
        return hostPort;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public Integer getProxyPort() {
        return proxyPort;
    }

    public Integer getNumOfRequestsPerParameter() {
        return numOfRequestsPerParameter;
    }

    public List<Integer> getUserProvidedPositiveResponseCodes() {
        return userProvidedPositiveResponseCodes;
    }

    public List<Integer> getUserProvidedNegativeResponseCodes() {
        return userProvidedNegativeResponseCodes;
    }

    @Override
    public Integer call() {
        TestConfiguration.initFrom(this);
        logger.info("Running with this configuration:\n{}", TestConfiguration.getWorkingConfigurationString());

        TestNG testNG = new TestNG();
        testNG.setOutputDirectory("build/testng-results");
        testNG.addListener(new TestListener());
        testNG.setTestClasses(new Class[] {
            com.imperva.apiattacktool.tests.MainTest.class});
        testNG.setDefaultSuiteName("API Attacks");
        testNG.setDefaultTestName("Full Suite");
        testNG.setVerbose(1);
        testNG.run();

        return testNG.getStatus();
    }
}
