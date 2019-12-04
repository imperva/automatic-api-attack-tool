package com.imperva.apiattacktool.model.tests;

import com.imperva.apiattacktool.tests.TestConfiguration;

import java.util.Collection;

public class NegativeTestHttpResponseValidator extends BaseHttpResponseValidator {

    public NegativeTestHttpResponseValidator(Collection<Integer> httpResponseCodesList) {
        super(httpResponseCodesList);
    }

    @Override
    public boolean isValidHttpCode(int httpResponseCode) {
        if (TestConfiguration.getUserProvidedNegativeResponseCodes().contains(httpResponseCode)) {
            return true;
        }
        if (httpResponseCode < 300 || httpResponseCode >= 500) {
            return false;
        }
        if (getHttpResponseCodes().contains(0)) {
            return true;
        }

        return getHttpResponseCodes().contains(httpResponseCode);
    }

    @Override
    public String toString() {
        return buildToString(TestConfiguration.getUserProvidedNegativeResponseCodes(), "1xx,2xx,5xx");
    }
}
