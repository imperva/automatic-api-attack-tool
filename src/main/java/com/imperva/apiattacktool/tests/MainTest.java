package com.imperva.apiattacktool.tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imperva.apiattacktool.model.tests.HttpResponseValidator;
import com.imperva.apiattacktool.utils.TestReporter;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.RequestLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.util.EntityUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import static org.apache.http.Consts.UTF_8;

public class MainTest {

    private CloseableHttpClient httpClient;

    @BeforeClass
    public void initParameters() {
        TestReporter.log("************ Imperva API Attack Tool ************");
        TestReporter.log("************ *********************** ************");
        TestReporter.log("************ Luke, I am your Fuzzer  ************");
        TestReporter.log("************ *********************** ************");

        //Init HttpClient
        PoolingHttpClientConnectionManager poolingConnManager = new PoolingHttpClientConnectionManager();
        poolingConnManager.setMaxTotal(10);
        httpClient = HttpClients.custom().setConnectionManager(poolingConnManager).build();
    }

    @Test(dataProvider = "positiveScenarioDataProvider", dataProviderClass = ScenariosDataProvider.class)
    public void positiveAllProperties(HttpRequest httpRequest, HttpResponseValidator httpResponseValidator, String testComment, String testId) throws IOException {
        testEndpoint(httpRequest, httpResponseValidator, testComment, testId);
    }

    @Test(dataProvider = "positiveRequiredPropertiesOnlyScenarioDataProvider", dataProviderClass = ScenariosDataProvider.class,
        priority = 1)
    public void positiveRequiredProperties(HttpRequest httpRequest, HttpResponseValidator httpResponseValidator, String testComment, String testId) throws IOException {
        testEndpoint(httpRequest, httpResponseValidator, testComment, testId);
    }

    @Test(dataProvider = "negativeBadPropertyScenarioDataProvider", dataProviderClass = ScenariosDataProvider.class,
        priority = 2)
    public void negativeBadProperty(HttpRequest httpRequest, HttpResponseValidator httpResponseValidator, String testComment, String testId) throws IOException {
        testEndpoint(httpRequest, httpResponseValidator, "Bad " + testComment, testId);
    }

    private void testEndpoint(HttpRequest httpRequest, HttpResponseValidator httpResponseValidator, String testComment, String testId) throws IOException {
        TestReporter.log("***** Testing API Endpoint *****");
        TestReporter.log("***** Test ID: " + testId);
        TestReporter.log("Testing: " + testComment);

        printHttpRequestToLog(httpRequest, getPrintToLogAction());

        HttpHost httpHost;
        if (TestConfiguration.isProxyDefined()) {
            httpHost = new HttpHost(TestConfiguration.getProxyHost(), TestConfiguration.getProxyPort(), TestConfiguration.getHostScheme());
            httpRequest.addHeader("Host", TestConfiguration.getHostName()); // This is since we address the proxy and want it to forward to our host
        } else {
            httpHost = new HttpHost(TestConfiguration.getHostName(), TestConfiguration.getHostPort(), TestConfiguration.getHostScheme());
        }

        httpRequest.addHeader("Accept", "application/json"); // We'd like to get a json response
        CloseableHttpResponse httpResponse;
        try {
            httpResponse = httpClient.execute(httpHost, httpRequest);
        } catch (Exception anyException) {
            TestReporter.log("❌ Error connecting to target! " + anyException.getMessage());
            TestReporter.log("");
            Assert.fail(anyException.getMessage());
            return;
        }

        int statusCode = httpResponse.getStatusLine().getStatusCode();
        boolean isStatusCodeValid = httpResponseValidator.isValidHttpCode(statusCode);
        TestReporter.log((isStatusCodeValid? "✅ " : "❌ ") +"Request was: " + httpRequest.toString() + ", Response status code: " + statusCode + (isStatusCodeValid? " " : " (UNEXPECTED)"));
        if (!isStatusCodeValid) {
            TestReporter.log(httpResponseValidator.toString());
        }

        printHttpResponse(httpResponse);
        httpResponse.close();
        TestReporter.log("");

        // Storing request in a file, for debugging purposes later on
        if (!isStatusCodeValid) {
            saveHttpRequestToFile(httpRequest, testId);
        }
        if (!isStatusCodeValid) {
            Assert.fail("Unexpected response code: " + statusCode);
        }
    }

    private void printHttpRequestToLog(HttpRequest httpRequest, PrintAction printAction) {
        if (httpRequest instanceof BasicHttpRequest) {
            BasicHttpRequest basicHttpRequest = (BasicHttpRequest) httpRequest;
            RequestLine requestLine = basicHttpRequest.getRequestLine();

            printAction.print("--> Url: " + requestLine.getUri());
            printAction.print("--> Method: " + requestLine.getMethod());
            printAction.print("--> Headers: " + Arrays.toString(basicHttpRequest.getAllHeaders()));

            if (httpRequest instanceof BasicHttpEntityEnclosingRequest) {
                BasicHttpEntityEnclosingRequest basicHttpEntityEnclosingRequest = (BasicHttpEntityEnclosingRequest) httpRequest;

                String body = null;

                try {
                    body = EntityUtils.toString(basicHttpEntityEnclosingRequest.getEntity(), "UTF-8");
                    printAction.print("--> Body: " + body);
                    printAction.print("----------End of Body----------");
                } catch (Exception exception) {
                    printAction.print("--> Body: empty (" + exception.getMessage() + ")");
                }
            }
            printAction.print("");
        } else {
            printAction.print("--> Endpoint: " + httpRequest.toString());
        }
    }

    private void saveHttpRequestToFile(HttpRequest httpRequest, String testId) {
        String folderName = "bad_requests";
        new File(folderName).mkdirs(); // Makes sure the directory exists
        String fileName = folderName + "/" + testId;
        try (PrintWriter printWriter = new PrintWriter(fileName, "UTF-8")) {
            printHttpRequestToLog(httpRequest, new PrintAction() {
                @Override
                public void print(String message) {
                    printWriter.println(message);
                }

                @Override
                public void print(Exception exception) {
                    printWriter.println(exception.getMessage());
                }
            });
        } catch (FileNotFoundException exception) {
            TestReporter.log("Could not write file named: " +  fileName);
            TestReporter.log(exception);
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
            TestReporter.log("Could not write file. Unsupported encoding!");
            TestReporter.log(unsupportedEncodingException);
        }
    }

    private void printHttpResponse(CloseableHttpResponse closeableHttpResponse) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String responseBody = EntityUtils.toString(closeableHttpResponse.getEntity(), UTF_8);
        TestHttpResponse testHttpResponse;
        try {
            JsonNode getApiJsonBody = objectMapper.readTree(responseBody);
            testHttpResponse = objectMapper.readValue(getApiJsonBody.toString(), TestHttpResponse.class);
        } catch (Exception ex) {
            TestReporter.log("Response (non parsed):");
            TestReporter.log(responseBody);
            return;
        }
        TestReporter.log("Response (parsed):");
        TestReporter.log(testHttpResponse == null? "empty" : testHttpResponse);
    }

    interface PrintAction {
        void print(String message);
        void print(Exception exception);
    }

    private PrintAction getPrintToLogAction() {
        return new PrintAction() {
            @Override
            public void print(String message) {
                TestReporter.log(message);
            }

            @Override
            public void print(Exception exception) {
                TestReporter.log(exception);
            }
        };
    }
}
