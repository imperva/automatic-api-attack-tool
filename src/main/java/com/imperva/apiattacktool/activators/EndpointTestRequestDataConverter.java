package com.imperva.apiattacktool.activators;

import com.google.gson.Gson;
import com.imperva.apiattacktool.model.tests.EndpointTestRequestData;
import com.imperva.apiattacktool.model.tests.ParameterLocation;
import com.imperva.apiattacktool.model.valued.ArrayPropertyValue;
import com.imperva.apiattacktool.model.valued.ArrayPropertyValueAdapter;
import com.imperva.apiattacktool.model.valued.EndpointValuedModel;
import com.imperva.apiattacktool.model.valued.PropertyValue;
import com.imperva.apiattacktool.model.valued.PropertyValueNode;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

public class EndpointTestRequestDataConverter implements RequestDataConverter {

    private final Gson gson;

    public EndpointTestRequestDataConverter() {
        gson = new Gson();
    }

    @Override
    public List<EndpointTestRequestData> processList(List<EndpointValuedModel> endpointModelList) {
        if (endpointModelList == null) {
            return Collections.emptyList();
        }

        List<EndpointTestRequestData> endpointTestRequestDataList = endpointModelList.stream()
            .map(this::convertFrom)
            .collect(Collectors.toList());
        return endpointTestRequestDataList;
    }

    private EndpointTestRequestData convertFrom(EndpointValuedModel endpointValuedModel) {
        Map<ParameterLocation, List<PropertyValue>> parameterLocationToPropertyValueMap =
            endpointValuedModel.getPropertiesMap().values().stream()
                .filter(propertyValue -> propertyValue.getParameterLocation() != ParameterLocation.BODY)
                .map(propertyValue -> propertyValue instanceof ArrayPropertyValue ? new ArrayPropertyValueAdapter((ArrayPropertyValue) propertyValue) :
                    propertyValue)
                .collect(Collectors.groupingBy(PropertyValue::getParameterLocation));

        Optional<Map.Entry<String, PropertyValueNode>> bodyParamEntry =
            endpointValuedModel.getChildrenMap().entrySet().stream()
                .filter(stringPropertyValueNodeEntry -> stringPropertyValueNodeEntry.getValue().getParameterLocation() == ParameterLocation.BODY)
                .findFirst();

        String bodyParamString = null;
        if (bodyParamEntry.isPresent()) {
            Map<String, Object> bodyParamMapRepresentation = bodyParamEntry.get().getValue().bodyParameterJsonRepresentationMap();
            bodyParamString = gson.toJson(bodyParamMapRepresentation);
        }

        String urlWithInjectedValues =
            getUrlInjectedWithValues(endpointValuedModel.getFullPathWithParamBrackets(), parameterLocationToPropertyValueMap.get(ParameterLocation.PATH));
        EndpointTestRequestData endpointTestRequestData =
            new EndpointTestRequestData(endpointValuedModel.getHttpMethod(), endpointValuedModel.getFullPathWithParamBrackets(), urlWithInjectedValues,
                parameterLocationToPropertyValueMap, bodyParamString, endpointValuedModel.getConsumes(), endpointValuedModel.getTestComment(),
                endpointValuedModel.getHttpResponseCodesCollection());
        return endpointTestRequestData;
    }

    private String getUrlInjectedWithValues(String urlWithPathParametersInBrackets, List<PropertyValue> pathParametersList) {
        if (pathParametersList == null) {
            return urlWithPathParametersInBrackets;
        }

        String injectedString = urlWithPathParametersInBrackets;
        for (PropertyValue propertyValue : pathParametersList) {
            injectedString = injectedString.replaceAll(
                getPathParameterPatternToReplace(propertyValue.getName()), Matcher.quoteReplacement(String.valueOf(propertyValue.getValue())));
        }
        return injectedString;
    }

    /**
     * @param pathParamName Path parameter name
     * @return Path parameter pattern
     */
    private String getPathParameterPatternToReplace(String pathParamName) {
        return "\\{" + pathParamName + "}";
    }
}
