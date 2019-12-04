package com.imperva.apiattacktool.tests;

import com.imperva.apiattacktool.activators.HttpRequestGenerator;
import com.imperva.apiattacktool.activators.ModelToValueConverter;
import com.imperva.apiattacktool.activators.RequestDataConverter;
import com.imperva.apiattacktool.fuzzing.Fuzzer;
import com.imperva.apiattacktool.fuzzing.parameters.PolicyEnforcer;
import com.imperva.apiattacktool.processors.EndpointModelProcessor;
import com.imperva.apispecparser.exceptions.ParseException;
import com.imperva.apispecparser.model.EndpointModel;
import com.imperva.apispecparser.parsers.ApiSpecFileLocation;
import com.imperva.apispecparser.parsers.swagger.Swagger2Parser;
import com.imperva.apispecparser.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class AbstractTestDriver {
    private static final Logger logger = LoggerFactory.getLogger(AbstractTestDriver.class);

    private ModelToValueConverter modelToValueConverter;
    private PolicyEnforcer policyEnforcer;
    private EndpointModelProcessor beforeMainEndpointModelProcessor;
    private EndpointModelProcessor mainEndpointModelProcessor;
    private RequestDataConverter testRequestDataConverter;
    private HttpRequestGenerator httpRequestGenerator;
    private Fuzzer fuzzer;

    public AbstractTestDriver(ModelToValueConverter modelToValueConverter,
                              PolicyEnforcer policyEnforcer,
                              EndpointModelProcessor beforeMainEndpointModelProcessor,
                              EndpointModelProcessor mainEndpointModelProcessor,
                              RequestDataConverter testRequestDataConverter,
                              HttpRequestGenerator httpRequestGenerator,
                              Fuzzer fuzzer) {
        this.modelToValueConverter = modelToValueConverter;
        this.policyEnforcer = policyEnforcer;
        this.beforeMainEndpointModelProcessor = beforeMainEndpointModelProcessor;
        this.mainEndpointModelProcessor = mainEndpointModelProcessor;
        this.testRequestDataConverter = testRequestDataConverter;
        this.httpRequestGenerator = httpRequestGenerator;
        this.fuzzer = fuzzer;
    }

    public ModelToValueConverter getModelToValueConverter() {
        return modelToValueConverter;
    }

    public PolicyEnforcer getPolicyEnforcer() {
        return policyEnforcer;
    }

    public EndpointModelProcessor getBeforeMainEndpointModelProcessor() {
        return beforeMainEndpointModelProcessor;
    }

    public EndpointModelProcessor getMainEndpointModelProcessor() {
        return mainEndpointModelProcessor;
    }

    public RequestDataConverter getTestRequestDataConverter() {
        return testRequestDataConverter;
    }

    public HttpRequestGenerator getHttpRequestGenerator() {
        return httpRequestGenerator;
    }

    public Fuzzer getFuzzer() {
        return fuzzer;
    }

    protected String loadResourceFileAsString(String resourceFileName) throws IOException {
        return FileUtils.readResource(resourceFileName);
    }

    protected List<EndpointModel> parseSwagger(String resourceFileName) {
        List<EndpointModel> endpointModelList;
        Swagger2Parser swagger2Parser = new Swagger2Parser();

        try {
            endpointModelList = swagger2Parser.getEndpointModelList(resourceFileName, ApiSpecFileLocation.EXTERNAL);
        } catch (ParseException parseException) {
            logger.error("Error parsing swagger: {}", parseException.getMessage());
            return Collections.emptyList();
        }
        return endpointModelList;
    }
}
