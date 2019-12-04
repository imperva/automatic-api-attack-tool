package com.imperva.apiattacktool.fuzzing.modelgenerators;

import com.imperva.apiattacktool.fuzzing.Fuzzer;
import com.imperva.apiattacktool.model.valued.EndpointValuedModel;
import com.imperva.apiattacktool.model.valued.PropertyValue;
import com.imperva.apiattacktool.model.valued.PropertyValueNode;
import com.imperva.apiattacktool.model.valued.factory.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CloningIterativeFuzzedModelsGenerator implements FuzzedModelsGenerator {
    private static final Logger logger = LoggerFactory.getLogger(CloningIterativeFuzzedModelsGenerator.class);

    private PropertyValueFactory propertyValueFactory;

    public CloningIterativeFuzzedModelsGenerator(PropertyValueFactory propertyValueFactory) {
        this.propertyValueFactory = propertyValueFactory;
    }

    /**
     * Requires an already fuzzed endpointValueModel, since assumes all other values in iteration are already set
     *
     * @param endpointValuedModel
     * @param fuzzer
     * @return
     */
    @Override
    public List<EndpointValuedModel> fuzzModelValues(EndpointValuedModel endpointValuedModel, Fuzzer fuzzer) {
        if (endpointValuedModel == null) {
            return Collections.emptyList();
        }

        List<EndpointValuedModel> fuzzedEndpointValuedModelList = fuzzModelValue(endpointValuedModel, fuzzer);
        return fuzzedEndpointValuedModelList;
    }

    @Override
    public PropertyValueFactory getPropertyValueFactory() {
        return propertyValueFactory;
    }


    private List<EndpointValuedModel> fuzzModelValue(EndpointValuedModel endpointValuedModel, Fuzzer fuzzer) {
        EndpointValuedModel clonedEndpointValueModel;
        try {
            clonedEndpointValueModel = (EndpointValuedModel) endpointValuedModel.clone();
        } catch (CloneNotSupportedException cloneException) {
            logger.error("Clone of endpointValuedModel failed: {} {}",
                endpointValuedModel.getHttpMethod(), endpointValuedModel.getFullPathWithParamBrackets());
            clonedEndpointValueModel = endpointValuedModel;
        }

        List<EndpointValuedModel> fuzzedEndpointModelValuesList =
            traverseFuzzAndInjectPropertyValueNode(clonedEndpointValueModel, clonedEndpointValueModel.getPropertyValueNode(), fuzzer, null);
        return fuzzedEndpointModelValuesList;
    }

    private List<EndpointValuedModel> fuzzAndInjectPropertyValues(EndpointValuedModel referenceModel, Map<String, PropertyValue> nameToPropertyValueMap,
                                                                  Fuzzer fuzzer, String relativePathForPropertyName) {
        List<EndpointValuedModel> resultList = new LinkedList<>();

        nameToPropertyValueMap.entrySet().forEach(
            stringToPropertyValueEntry -> {
                PropertyValue propertyValue = stringToPropertyValueEntry.getValue();
                List<?> valuesList = propertyValue.fuzz(fuzzer);
                if (valuesList != null && !valuesList.isEmpty()) {
                    // Store current value
                    Object currentValue = stringToPropertyValueEntry.getValue().getValue();
                    // Set fuzzed value and clone
                    stringToPropertyValueEntry.getValue().setValue(valuesList.get(0));

                    EndpointValuedModel clonedReferenceModel;
                    try {
                        clonedReferenceModel = (EndpointValuedModel) referenceModel.clone();
                        clonedReferenceModel.setTestComment(
                            "Property: " + buildRelativePath(relativePathForPropertyName, stringToPropertyValueEntry.getKey())
                                + " (" + stringToPropertyValueEntry.getValue().getType() + "), value: " + stringToPropertyValueEntry.getValue().getValue()
                                + ", URL encoded: "
                                + URLEncoder.encode(String.valueOf(stringToPropertyValueEntry.getValue().getValue()), StandardCharsets.UTF_8.toString()));
                        resultList.add(clonedReferenceModel);
                    } catch (CloneNotSupportedException | UnsupportedEncodingException e) {
                        logger.error("Error cloning reference model: {}", referenceModel);
                        return;
                    }
                    // Put current value back
                    stringToPropertyValueEntry.getValue().setValue(currentValue);
                }
            });
        return resultList;
    }

    private List<EndpointValuedModel> traverseFuzzAndInjectPropertyValueNode(EndpointValuedModel referenceModel, PropertyValueNode propertyValueNode,
                                                                             Fuzzer fuzzer, String relativePathForPropertyName) {
        List<EndpointValuedModel> resultList = new LinkedList<>();
        if (relativePathForPropertyName == null) {
            relativePathForPropertyName = "";
        }

        if (propertyValueNode.getPropertiesMap().size() > 0) {
            resultList.addAll(fuzzAndInjectPropertyValues(referenceModel, propertyValueNode.getPropertiesMap(), fuzzer, relativePathForPropertyName));
        }

        if (propertyValueNode.getChildrenMap().size() > 0) {
            for (Map.Entry<String, PropertyValueNode> stringToPropertyNodeMap : propertyValueNode.getChildrenMap().entrySet()) {
                resultList.addAll(traverseFuzzAndInjectPropertyValueNode(referenceModel, stringToPropertyNodeMap.getValue(), fuzzer,
                    buildRelativePath(relativePathForPropertyName, stringToPropertyNodeMap.getKey())));
            }
        }
        return resultList;
    }

    private String buildRelativePath(String parent, String son) {
        return parent + "/" + son;
    }
}
