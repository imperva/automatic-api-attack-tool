package com.imperva.apiattacktool.model.tests;

import com.imperva.apiattacktool.model.valued.PropertyValue;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class EndpointTestRequestData {
    private HttpMethod httpMethod;
    private String fullPathWithParamBrackets; // This is for debug purposes
    private String endpointUrl;
    private Map<ParameterLocation, List<PropertyValue>> parameterLocationToParameterList;
    private String bodyString;
    private List<String> consumesMimeTypes;
    private String testComment;
    private Collection<Integer> httpResponseCodesCollection;

    public EndpointTestRequestData(HttpMethod httpMethod, String fullPathWithParamBrackets, String endpointUrl, Map<ParameterLocation,
        List<PropertyValue>> parameterLocationToParameterList, String bodyString, List<String> consumesMimeTypes, String testComment,
        Collection<Integer> httpResponseCodeList) {
        this.httpMethod = httpMethod;
        this.fullPathWithParamBrackets = fullPathWithParamBrackets;
        this.endpointUrl = endpointUrl;
        this.parameterLocationToParameterList = parameterLocationToParameterList;
        this.bodyString = bodyString;
        this.consumesMimeTypes = consumesMimeTypes;
        this.testComment = testComment;
        this.httpResponseCodesCollection = httpResponseCodeList;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getFullPathWithParamBrackets() {
        return fullPathWithParamBrackets;
    }

    public String getEndpointUrl() {
        return endpointUrl;
    }

    public boolean hasBodyParameters() {
        return bodyString != null && bodyString.length() > 0;
    }

    public boolean hasHeaderParameters() {
        return hasParameterInLocation(ParameterLocation.HEADER);
    }

    public boolean hasQueryParameters() {
        return hasParameterInLocation(ParameterLocation.QUERY);
    }

    public boolean hasCookieParameters() {
        return hasParameterInLocation(ParameterLocation.COOKIE);
    }

    public boolean hasFormDataParameters() {
        return hasParameterInLocation(ParameterLocation.FORMDATA);
    }

    public String getBodyParameter() {
        return bodyString;
    }

    public List<PropertyValue> getHeaderParameters() {
        return parameterLocationToParameterList.get(ParameterLocation.HEADER);
    }

    public List<PropertyValue> getQueryParameters() {
        return parameterLocationToParameterList.get(ParameterLocation.QUERY);
    }

    public List<PropertyValue> getFormDataParameters() {
        return parameterLocationToParameterList.get(ParameterLocation.FORMDATA);
    }

    public List<String> getConsumesMimeTypes() {
        return consumesMimeTypes;
    }

    public Collection<Integer> getHttpResponseCodesCollection() {
        return httpResponseCodesCollection;
    }

    private boolean hasParameterInLocation(ParameterLocation location) {
        return parameterLocationToParameterList.get(location) != null && parameterLocationToParameterList.get(location).size() > 0;
    }

    public String getTestComment() {
        return testComment;
    }

    @Override
    public String toString() {
        return "EndpointTestRequestData{"
            + httpMethod + " " + endpointUrl
            + ", parametersByLocation=" + parameterLocationToParameterList
            + ", bodyString='" + bodyString + '\''
            + '}';
    }
}
