package com.imperva.apiattacktool.activators;

import com.imperva.apiattacktool.model.tests.EndpointTestRequestData;
import com.imperva.apiattacktool.model.valued.EndpointValuedModel;

import java.util.List;

public interface RequestDataConverter {
    List<EndpointTestRequestData> processList(List<EndpointValuedModel> endpointModelList);
}
