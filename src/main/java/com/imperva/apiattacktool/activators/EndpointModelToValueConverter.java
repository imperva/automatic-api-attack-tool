package com.imperva.apiattacktool.activators;

import com.imperva.apiattacktool.model.valued.EndpointValuedModel;
import com.imperva.apiattacktool.model.valued.factory.PropertyValueFactory;
import com.imperva.apispecparser.model.EndpointModel;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EndpointModelToValueConverter implements ModelToValueConverter {

    private PropertyValueFactory propertyValueFactory;

    public EndpointModelToValueConverter(PropertyValueFactory propertyValueFactory) {
        this.propertyValueFactory = propertyValueFactory;
    }

    @Override
    public List<EndpointValuedModel> endpointModelToEndpointValuedModel(List<EndpointModel> endpointModelList) {
        List<EndpointValuedModel> endpointValuedModelList = Collections.EMPTY_LIST;
        if (endpointModelList != null) {
            endpointValuedModelList = endpointModelList.stream().map(
                endpointModel -> new EndpointValuedModel(endpointModel, propertyValueFactory))
                .collect(Collectors.toList());
        }
        return endpointValuedModelList;
    }
}
