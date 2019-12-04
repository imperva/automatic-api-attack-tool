package com.imperva.apiattacktool.model.tests;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class BaseHttpResponseValidator implements HttpResponseValidator {

    private Set<Integer> httpResponseCodes;

    public BaseHttpResponseValidator(Collection<Integer> httpResponseCodesList) {
        this.httpResponseCodes = new HashSet<>(httpResponseCodesList);
    }

    @Override
    public abstract boolean isValidHttpCode(int httpResponseCode);

    protected Set<Integer> getHttpResponseCodes() {
        return httpResponseCodes;
    }

    protected String getStringFromResponseCodeCollection(Collection<Integer> responseCodeCollection) {
        String commaSeparatedString =
            responseCodeCollection.stream()
                .filter(responseCode -> responseCode != 0)
                .map(responseCode -> String.valueOf(responseCode))
                .collect(Collectors.joining(","));
        if(responseCodeCollection.contains(0)) {
            commaSeparatedString = commaSeparatedString + (responseCodeCollection.size() > 1 ? ", " : "") + "any";
        }
        return commaSeparatedString;
    }

    protected String buildToString(Collection<Integer> userProvidedResponseCodeCollection, String scenarioOverride) {
        StringBuilder responseStringBuilder = new StringBuilder("Valid responses: ");
        String userProvidedString = getStringFromResponseCodeCollection(userProvidedResponseCodeCollection);
        if (!StringUtils.isBlank(userProvidedString)) {
            responseStringBuilder.append("(user override:");
            responseStringBuilder.append(userProvidedString);
            responseStringBuilder.append("), ");
        }
        if (!StringUtils.isBlank(responseStringBuilder)) {
            responseStringBuilder.append("(scenario override, not: ");
            responseStringBuilder.append(scenarioOverride);
            responseStringBuilder.append("), ");
        }
        responseStringBuilder.append(getStringFromResponseCodeCollection(getHttpResponseCodes()));

        return responseStringBuilder.toString();
    }
}
