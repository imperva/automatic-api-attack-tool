package com.imperva.apiattacktool.fuzzing.modelgenerators;

import com.imperva.apiattacktool.fuzzing.Fuzzer;
import com.imperva.apiattacktool.model.valued.EndpointValuedModel;
import com.imperva.apiattacktool.model.valued.PropertyValue;
import com.imperva.apiattacktool.model.valued.PropertyValueNode;
import com.imperva.apiattacktool.model.valued.factory.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
// TODO: Fuzzer should take MIME type (and it's corresponding charset) into account, when generating values for formData params
public class SingleValueFuzzedModelsGenerator implements FuzzedModelsGenerator {
    private static final Logger logger = LoggerFactory.getLogger(SingleValueFuzzedModelsGenerator.class);

    private PropertyValueFactory propertyValueFactory;

    public SingleValueFuzzedModelsGenerator(PropertyValueFactory propertyValueFactory) {
        this.propertyValueFactory = propertyValueFactory;
    }

    @Override
    public List<EndpointValuedModel> fuzzModelValues(EndpointValuedModel endpointValuedModel, Fuzzer fuzzer) {
        if (endpointValuedModel == null) {
            return Collections.emptyList();
        }

        List<EndpointValuedModel> endpointValuedModelList = new LinkedList<>();
        endpointValuedModelList.add(fuzzModelValue(endpointValuedModel, fuzzer));
        return endpointValuedModelList;
    }

    @Override
    public PropertyValueFactory getPropertyValueFactory() {
        return propertyValueFactory;
    }


    private EndpointValuedModel fuzzModelValue(EndpointValuedModel endpointValuedModel, Fuzzer fuzzer) {
        EndpointValuedModel clonedEndpointValueModel;
        try {
            clonedEndpointValueModel = (EndpointValuedModel) endpointValuedModel.clone();
        } catch (CloneNotSupportedException cloneException) {
            logger.error("Clone of endpointValuedModel failed: {} {}",
                endpointValuedModel.getHttpMethod(), endpointValuedModel.getFullPathWithParamBrackets());
            clonedEndpointValueModel = endpointValuedModel;
        }
        clonedEndpointValueModel.setTestComment(endpointValuedModel.getFullPathWithParamBrackets());
        fuzzAndInjectPropertyValues(clonedEndpointValueModel.getPropertiesMap(), fuzzer);
        clonedEndpointValueModel.getChildrenMap().entrySet().forEach(
            nameToPropertyValueNodeEntry -> {
                traverseFuzzAndInjectPropertyValueNode(nameToPropertyValueNodeEntry.getValue(), fuzzer);
            });
        return clonedEndpointValueModel;
    }

    private void fuzzAndInjectPropertyValues(Map<String, PropertyValue> nameToPropertyValueMap, Fuzzer fuzzer) {
        List<String> propertiesToDeleteList = new LinkedList<>();
        nameToPropertyValueMap.entrySet().forEach(
            stringToPropertyValueEntry -> {
                PropertyValue propertyValue = stringToPropertyValueEntry.getValue();
                List<? extends PropertyValue> valuesList = propertyValue.fuzz(fuzzer);
                if (valuesList != null && !valuesList.isEmpty()) {
                    stringToPropertyValueEntry.getValue().setValue(valuesList.get(0));
                } else {
                    propertiesToDeleteList.add(stringToPropertyValueEntry.getKey());
                }
            });
        propertiesToDeleteList.forEach(name -> nameToPropertyValueMap.remove(name));
    }

    private void traverseFuzzAndInjectPropertyValueNode(PropertyValueNode propertyValueNode, Fuzzer fuzzer) {

        if (propertyValueNode.getPropertiesMap().size() > 0) {
            fuzzAndInjectPropertyValues(propertyValueNode.getPropertiesMap(), fuzzer);
        }

        if (propertyValueNode.getChildrenMap().size() > 0) {
            propertyValueNode.getChildrenMap().entrySet().forEach(
                stringToPropertyNodeMap ->
                    traverseFuzzAndInjectPropertyValueNode(stringToPropertyNodeMap.getValue(), fuzzer)
            );
        }
    }
}
