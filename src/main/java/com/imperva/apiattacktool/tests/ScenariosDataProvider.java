package com.imperva.apiattacktool.tests;

import com.imperva.apiattacktool.fuzzing.Fuzzer;
import com.imperva.apiattacktool.fuzzing.parameters.AllParametersPolicyEnforcer;
import com.imperva.apiattacktool.fuzzing.parameters.PolicyEnforcer;
import com.imperva.apiattacktool.fuzzing.parameters.RequiredOnlyPolicyEnforcer;
import com.imperva.apiattacktool.fuzzing.value.NegativeSingleValueFuzzer;
import com.imperva.apiattacktool.fuzzing.value.PositiveSingleValueFuzzer;
import com.imperva.apiattacktool.model.tests.HttpRequestWrapper;
import com.imperva.apiattacktool.model.valued.factory.PropertyValueFactory;
import com.imperva.apiattacktool.model.valued.factory.SimplePropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ScenariosDataProvider {
    private static final Logger logger = LoggerFactory.getLogger(ScenariosDataProvider.class);

    @DataProvider()
    public static Object[][] positiveScenarioDataProvider() {
        PropertyValueFactory propertyValueFactory = new SimplePropertyValueFactory();
        Fuzzer fuzzer = new PositiveSingleValueFuzzer(propertyValueFactory);
        PolicyEnforcer policyEnforcer = new AllParametersPolicyEnforcer();
        SingleValueScenarioTestDriver singleValueScenarioTestDriver =
            new SingleValueScenarioTestDriver(fuzzer, policyEnforcer, propertyValueFactory);
        return getEndpointTestRequestData(singleValueScenarioTestDriver);
    }

    @DataProvider()
    public static Object[][] positiveRequiredPropertiesOnlyScenarioDataProvider() {
        PropertyValueFactory propertyValueFactory = new SimplePropertyValueFactory();
        Fuzzer fuzzer = new PositiveSingleValueFuzzer(propertyValueFactory);
        PolicyEnforcer policyEnforcer = new RequiredOnlyPolicyEnforcer();
        SingleValueScenarioTestDriver singleValueScenarioTestDriver =
            new SingleValueScenarioTestDriver(fuzzer, policyEnforcer, propertyValueFactory);
        return getEndpointTestRequestData(singleValueScenarioTestDriver);
    }

    @DataProvider()
    public static Object[][] negativeBadPropertyScenarioDataProvider(ITestContext context) {
        PropertyValueFactory propertyValueFactory = new SimplePropertyValueFactory();
        PolicyEnforcer policyEnforcer = new AllParametersPolicyEnforcer();
        Fuzzer firstStepFuzzer = new PositiveSingleValueFuzzer(propertyValueFactory);
        Fuzzer mainStepFuzzer = new NegativeSingleValueFuzzer(propertyValueFactory);
        NegativeSinglePropertyScenarioTestDriver negativeSinglePropertyScenarioTestDriver =
            new NegativeSinglePropertyScenarioTestDriver(firstStepFuzzer, mainStepFuzzer, policyEnforcer, propertyValueFactory);
        return getEndpointTestRequestData(negativeSinglePropertyScenarioTestDriver);
    }

    private static Object[][] getEndpointTestRequestData(TestDriver testDriver) {
        List<HttpRequestWrapper> httpRequestWrapperList;
        try {
            httpRequestWrapperList = testDriver.getHttpRequestList(TestConfiguration.getSpecFilePath(), TestConfiguration.getNumOfRequestsPerParameter());
        } catch (Exception anyException) {
            logger.error("Failed to get httpRequestList, for file: {}", TestConfiguration.getSpecFilePath(), anyException);
            return null;
        }

        Object[][] result = new Object[httpRequestWrapperList.size()][4];
        for (int i = 0; i < httpRequestWrapperList.size(); i++) {
            try {
                result[i][0] = httpRequestWrapperList.get(i).getHttpRequest();
                result[i][1] = httpRequestWrapperList.get(i).getHttpResponseValidator();
                result[i][2] = httpRequestWrapperList.get(i).getTestElementComment();
                result[i][3] = getTestId();
            } catch (Exception anyException) {
                logger.error("Error injecting data for test.", anyException);
            }
        }

        return result;
    }

    private static String getTestId() {
        return System.currentTimeMillis() + "-" + ThreadLocalRandom.current().nextInt(100000);
    }
}
