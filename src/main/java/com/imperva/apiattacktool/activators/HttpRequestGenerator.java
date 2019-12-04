package com.imperva.apiattacktool.activators;

import com.imperva.apiattacktool.model.tests.EndpointTestRequestData;
import com.imperva.apiattacktool.model.tests.HttpRequestWrapper;

import java.util.List;

public interface HttpRequestGenerator {
    List<HttpRequestWrapper> generateFrom(List<EndpointTestRequestData> endpointTestRequestDataList);
}
