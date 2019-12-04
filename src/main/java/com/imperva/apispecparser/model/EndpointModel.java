package com.imperva.apispecparser.model;

import com.imperva.apiattacktool.model.tests.HttpMethod;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class EndpointModel {

    private String fullPathWithParamBrackets;
    private HttpMethod httpMethod;
    private List<String> consumes;
    private PropertyNode propertiesNode;
    private List<Integer> httpResponseCodesList;

    public EndpointModel(String fullPathWithParamBrackets, HttpMethod httpMethod, List<String> consumes) {
        this.fullPathWithParamBrackets = fullPathWithParamBrackets;
        this.httpMethod = httpMethod;
        this.consumes = consumes == null ? Collections.emptyList() : consumes;
        this.propertiesNode = new PropertyNode(null, false, null);
        this.httpResponseCodesList = new LinkedList<>();
    }

    public void addParameter(String name, Property property) {
        propertiesNode.getPropertiesMap().put(name, property);
    }

    public void addNode(String name, PropertyNode propertyNode) {
        propertiesNode.getChildrenMap().put(name, propertyNode);
    }

    public void addResponseCode(int responseCode) {
        this.httpResponseCodesList.add(responseCode);
    }

    public Map<String, Property> getSimpleParametersMap() {
        return propertiesNode.getPropertiesMap();
    }

    public String getFullPathWithParamBrackets() {
        return fullPathWithParamBrackets;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public List<String> getConsumes() {
        return consumes;
    }

    public Map<String, PropertyNode> getChildrenMap() {
        return propertiesNode.getChildrenMap();
    }

    public List<Integer> getHttpResponseCodesList() {
        return httpResponseCodesList;
    }

    public String toString() {
        return String.format("Endpoint: %s %s%nNodes:%n%s%nValues:%n%s",
            httpMethod, fullPathWithParamBrackets, propertiesNode.getChildrenMap(), propertiesNode.getPropertiesMap());
    }
}
