package com.imperva.apiattacktool.model.tests;

import com.imperva.apiattacktool.tests.TestConfiguration;

import java.util.Collection;

public class PositiveTestHttpResponseValidator extends BaseHttpResponseValidator {

    public PositiveTestHttpResponseValidator(Collection<Integer> httpResponseCodesList) {
        super(httpResponseCodesList);
    }

    @Override
    public boolean isValidHttpCode(int httpResponseCode) {
        if (TestConfiguration.getUserProvidedPositiveResponseCodes().contains(httpResponseCode)) {
            return true;
        }
        if (httpResponseCode >= 500) {
            return false;
        }
        if (getHttpResponseCodes().contains(0)) {
            return true;
        }

        return getHttpResponseCodes().contains(httpResponseCode);
    }

    @Override
    public String toString() {
        return buildToString(TestConfiguration.getUserProvidedPositiveResponseCodes(), "5xx");
    }
}
