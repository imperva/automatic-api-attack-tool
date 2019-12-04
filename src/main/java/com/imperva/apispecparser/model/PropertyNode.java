package com.imperva.apispecparser.model;

import com.imperva.apiattacktool.model.tests.ParameterLocation;

import java.util.HashMap;
import java.util.Map;

public class PropertyNode {

    private String name;
    private boolean isRequired;

    private ParameterLocation parameterLocation;
    private Map<String, Property> propertyNameToPropertyValueMap;
    private Map<String, PropertyNode> propertyNameToPropertyNodeMap;

    public PropertyNode(String name, boolean isRequired, ParameterLocation parameterLocation) {
        this.name = name;
        this.isRequired = isRequired;
        this.parameterLocation = parameterLocation;
        this.propertyNameToPropertyNodeMap = new HashMap<>();
        this.propertyNameToPropertyValueMap = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void addPropertyValueType(String propertyName, Property property) {
        propertyNameToPropertyValueMap.put(propertyName, property);
    }

    public void addPropertyNode(String propertyName, PropertyNode propertyNode) {
        propertyNameToPropertyNodeMap.put(propertyName, propertyNode);
    }

    public ParameterLocation getParameterLocation() {
        return parameterLocation;
    }

    public Map<String, Property> getPropertiesMap() {
        return propertyNameToPropertyValueMap;
    }

    public boolean hasChildren() {
        return propertyNameToPropertyNodeMap.size() > 0;
    }

    public Map<String, PropertyNode> getChildrenMap() {
        return propertyNameToPropertyNodeMap;
    }

    @Override
    public String toString() {
        return "{location='" + parameterLocation + '\''
            + ", properties='" + propertyNameToPropertyValueMap.toString() + '\''
            + ", children='" + propertyNameToPropertyNodeMap + '\'' + "}";
    }
}
