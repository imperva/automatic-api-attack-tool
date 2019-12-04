package com.imperva.apiattacktool.activators;

import com.imperva.apiattacktool.model.valued.EndpointValuedModel;
import com.imperva.apispecparser.model.EndpointModel;

import java.util.List;

public interface ModelToValueConverter {

    List<EndpointValuedModel> endpointModelToEndpointValuedModel(List<EndpointModel> endpointModelList);

}
