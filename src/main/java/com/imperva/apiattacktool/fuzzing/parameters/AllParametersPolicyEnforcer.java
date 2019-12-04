package com.imperva.apiattacktool.fuzzing.parameters;

import com.imperva.apiattacktool.model.valued.EndpointValuedModel;

import java.util.List;

public class AllParametersPolicyEnforcer implements PolicyEnforcer {
    @Override
    public List<EndpointValuedModel> enforcePolicyOn(List<EndpointValuedModel> endpointValuedModelList) {
        return endpointValuedModelList; // NO-OP
    }
}
