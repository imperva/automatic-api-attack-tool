package com.imperva.apiattacktool.fuzzing.parameters;

import com.imperva.apiattacktool.model.valued.EndpointValuedModel;

import java.util.List;

public interface PolicyEnforcer {
    List<EndpointValuedModel> enforcePolicyOn(List<EndpointValuedModel> endpointValuedModelList);
}
