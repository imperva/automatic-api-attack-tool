package com.imperva.apiattacktool.fuzzing.parameters;

import com.imperva.apiattacktool.model.valued.EndpointValuedModel;
import com.imperva.apiattacktool.model.valued.PropertyValue;
import com.imperva.apiattacktool.model.valued.PropertyValueNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Makes sure the supplied endpointValueModels only have required parameters and properties.
 */
public class RequiredOnlyPolicyEnforcer implements PolicyEnforcer {
    private static final Logger logger = LoggerFactory.getLogger(RequiredOnlyPolicyEnforcer.class);

    @Override
    public List<EndpointValuedModel> enforcePolicyOn(List<EndpointValuedModel> endpointValuedModelList) {
        if (endpointValuedModelList == null) {
            return Collections.emptyList();
        }

        List<EndpointValuedModel> processedEndpointValueModelList =
            endpointValuedModelList.stream()
                .map(endpointValuedModel -> processModel(endpointValuedModel))
                .collect(Collectors.toList());
        return processedEndpointValueModelList;
    }

    private EndpointValuedModel processModel(EndpointValuedModel endpointValuedModel) {
        EndpointValuedModel clonedEndpointValueModel;
        try {
            clonedEndpointValueModel = (EndpointValuedModel) endpointValuedModel.clone();
        } catch (CloneNotSupportedException cloneException) {
            logger.error("Clone of endpointValuedModel failed: {} {}",
                endpointValuedModel.getHttpMethod(), endpointValuedModel.getFullPathWithParamBrackets());
            clonedEndpointValueModel = endpointValuedModel;
        }

        PropertyValueNode processedPropertyValueNode = processPropertyValueNode(clonedEndpointValueModel.getPropertyValueNode());
        clonedEndpointValueModel.setPropertyValueNode(processedPropertyValueNode);
        return clonedEndpointValueModel;
    }

    private Map<String, PropertyValue> processPropertyValues(Map<String, PropertyValue> nameToPropertyValueMap) {
        Map<String, PropertyValue> processedPropertiesMap = nameToPropertyValueMap.entrySet().stream()
            .filter(nameStringToPropertyValue -> nameStringToPropertyValue.getValue().isRequired())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return processedPropertiesMap;
    }

    private PropertyValueNode processPropertyValueNode(PropertyValueNode propertyValueNode) {
        PropertyValueNode newPropertyValueNode = new PropertyValueNode(propertyValueNode.getParameterLocation(), propertyValueNode.isRequired());
        newPropertyValueNode.setPropertiesMap(propertyValueNode.getPropertiesMap());
        newPropertyValueNode.setChildrenMap(propertyValueNode.getChildrenMap());

        if (propertyValueNode.getPropertiesMap().size() > 0) {
            Map<String, PropertyValue> processedPropertiesMap = processPropertyValues(propertyValueNode.getPropertiesMap());
            newPropertyValueNode.setPropertiesMap(processedPropertiesMap);
        }

        if (propertyValueNode.getChildrenMap().size() > 0) {
            Map<String, PropertyValueNode> processedChildrenMap = propertyValueNode.getChildrenMap().entrySet().stream()
                .filter(entry -> entry.getValue().isRequired())
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> processPropertyValueNode(entry.getValue())));
            newPropertyValueNode.setChildrenMap(processedChildrenMap);
        }
        return newPropertyValueNode;
    }
}
