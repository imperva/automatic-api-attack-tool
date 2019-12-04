package com.imperva.apiattacktool.model.valued;

import com.imperva.apiattacktool.model.tests.ParameterLocation;
import com.imperva.apiattacktool.model.valued.factory.PropertyValueFactory;
import com.imperva.apispecparser.model.Property;
import com.imperva.apispecparser.model.PropertyNode;

import java.util.HashMap;
import java.util.Map;

public class PropertyValueNode implements Cloneable {

    private ParameterLocation parameterLocation;
    private boolean isRequired;
    private Map<String, PropertyValue> propertyNameToPropertyValueMap;
    private Map<String, PropertyValueNode> propertyNameToPropertyValueNodeMap;

    public PropertyValueNode(ParameterLocation parameterLocation, boolean isRequired) {
        this.parameterLocation = parameterLocation;
        this.isRequired = isRequired;
        this.propertyNameToPropertyValueNodeMap = new HashMap<>();
        this.propertyNameToPropertyValueMap = new HashMap<>();
    }

    public PropertyValueNode(PropertyNode propertyNode, PropertyValueFactory propertyValueFactory) {
        PropertyValueNode propertyValueNode = traverseNodeConvertToPropertyValue(propertyNode, propertyValueFactory);
        this.parameterLocation = propertyValueNode.getParameterLocation();
        this.isRequired = propertyValueNode.isRequired();
        this.propertyNameToPropertyValueMap = propertyValueNode.propertyNameToPropertyValueMap;
        this.propertyNameToPropertyValueNodeMap = propertyValueNode.propertyNameToPropertyValueNodeMap;
    }

    public void addPropertyValueType(String propertyName, PropertyValue property) {
        propertyNameToPropertyValueMap.put(propertyName, property);
    }

    public void addPropertyNode(String propertyName, PropertyValueNode propertyNode) {
        propertyNameToPropertyValueNodeMap.put(propertyName, propertyNode);
    }

    public ParameterLocation getParameterLocation() {
        return parameterLocation;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public Map<String, PropertyValue> getPropertiesMap() {
        return propertyNameToPropertyValueMap;
    }

    public void setPropertiesMap(Map<String, PropertyValue> propertyNameToPropertyValueMap) {
        this.propertyNameToPropertyValueMap = propertyNameToPropertyValueMap;
    }

    public boolean hasChildren() {
        return propertyNameToPropertyValueNodeMap.size() > 0;
    }

    public Map<String, PropertyValueNode> getChildrenMap() {
        return propertyNameToPropertyValueNodeMap;
    }

    public void setChildrenMap(Map<String, PropertyValueNode> propertyNameToPropertyValueNodeMap) {
        this.propertyNameToPropertyValueNodeMap = propertyNameToPropertyValueNodeMap;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        PropertyValueNode propertyValueNode = new PropertyValueNode(this.parameterLocation, this.isRequired);

        for (Map.Entry<String, PropertyValue> entry : propertyNameToPropertyValueMap.entrySet()) {
            propertyValueNode.propertyNameToPropertyValueMap.put(entry.getKey(), (PropertyValue) entry.getValue().clone());
        }

        for (Map.Entry<String, PropertyValueNode> entry : propertyNameToPropertyValueNodeMap.entrySet()) {
            propertyValueNode.propertyNameToPropertyValueNodeMap.put(entry.getKey(), (PropertyValueNode) entry.getValue().clone());
        }

        return propertyValueNode;
    }

    public Map<String, Object> bodyParameterJsonRepresentationMap() {
        Map<String, Object> jsonRepresentationMap =
            new HashMap<>(propertyNameToPropertyValueMap.size() + propertyNameToPropertyValueNodeMap.size());
        propertyNameToPropertyValueMap.entrySet().stream()
            .filter(stringPropertyValueEntry -> stringPropertyValueEntry.getValue().getParameterLocation() == ParameterLocation.BODY)
            .forEach(stringToPropertyValueEntry ->
                jsonRepresentationMap.putAll(stringToPropertyValueEntry.getValue().bodyParameterJsonRepresentationMap()));

        propertyNameToPropertyValueNodeMap.entrySet().stream()
            .filter(stringPropertyValueEntry -> stringPropertyValueEntry.getValue().getParameterLocation() == ParameterLocation.BODY)
            .forEach(
                stringPropertyToValueNodeEntry ->
                    jsonRepresentationMap.put(stringPropertyToValueNodeEntry.getKey(),
                        stringPropertyToValueNodeEntry.getValue().bodyParameterJsonRepresentationMap()));
        return jsonRepresentationMap;
    }

    @Override
    public String toString() {
        return "PropertyValueNode{"
            + "propertyNameToPropertyValueMap=" + propertyNameToPropertyValueMap
            + ", propertyNameToPropertyValueNodeMap=" + propertyNameToPropertyValueNodeMap
            + '}';
    }

    private PropertyValue convertPropertyToPropertyValue(Property property, PropertyValueFactory propertyValueFactory) {
        return propertyValueFactory.getPropertyValueFromProperty(property);
    }

    private PropertyValueNode traverseNodeConvertToPropertyValue(PropertyNode propertyNode, PropertyValueFactory propertyValueFactory) {
        PropertyValueNode propertyValueNode = new PropertyValueNode(propertyNode.getParameterLocation(), propertyNode.isRequired());
        if (propertyNode.getPropertiesMap().size() > 0) {
            propertyNode.getPropertiesMap().entrySet().forEach(
                locatorToPropertyMapEntry -> {
                    propertyValueNode.addPropertyValueType(locatorToPropertyMapEntry.getKey(),
                        convertPropertyToPropertyValue(locatorToPropertyMapEntry.getValue(), propertyValueFactory));
                });
        }
        if (propertyNode.hasChildren()) {
            propertyNode.getChildrenMap().entrySet().forEach(
                locatorToPropertyNodeMap -> {
                    PropertyValueNode convertedPropertyValueNode = traverseNodeConvertToPropertyValue(locatorToPropertyNodeMap.getValue(),
                        propertyValueFactory);
                    propertyValueNode.addPropertyNode(locatorToPropertyNodeMap.getKey(), convertedPropertyValueNode);
                }
            );
        }
        return propertyValueNode;
    }

}
