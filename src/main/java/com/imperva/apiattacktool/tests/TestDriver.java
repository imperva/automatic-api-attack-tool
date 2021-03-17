package com.imperva.apiattacktool.tests;

import com.imperva.apiattacktool.model.tests.HttpRequestWrapper;

import java.util.List;

public interface TestDriver {
    List<HttpRequestWrapper> getHttpRequestList(String resourceFileName, int numOfRequestsPerParameter);
}
