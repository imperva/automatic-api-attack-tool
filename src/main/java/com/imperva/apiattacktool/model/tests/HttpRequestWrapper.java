package com.imperva.apiattacktool.model.tests;

import org.apache.http.HttpRequest;

public class HttpRequestWrapper {

    private HttpRequest httpRequest;
    private String testElementComment;
    private HttpResponseValidator httpResponseValidator;

    public HttpRequestWrapper(HttpRequest httpRequest, String testElementComment, HttpResponseValidator httpResponseValidator) {
        this.httpRequest = httpRequest;
        this.testElementComment = testElementComment;
        this.httpResponseValidator = httpResponseValidator;
    }

    public HttpRequest getHttpRequest() {
        return httpRequest;
    }

    public String getTestElementComment() {
        return testElementComment;
    }

    public HttpResponseValidator getHttpResponseValidator() {
        return httpResponseValidator;
    }
}
