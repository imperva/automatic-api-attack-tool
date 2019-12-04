package com.imperva.apiattacktool.model.valued;

import com.imperva.apiattacktool.model.tests.HttpMethod;
import com.imperva.apiattacktool.model.valued.factory.PropertyValueFactory;
import com.imperva.apispecparser.model.EndpointModel;
import com.imperva.apispecparser.model.Property;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EndpointValuedModel implements Cloneable {

    private String fullPathWithParamBrackets;
    private HttpMethod httpMethod;
    private List<String> consumes;
    private PropertyValueNode propertyValueNode;
    private String testComment = "";
    private Collection<Integer> httpResponseCodesCollection;

    public EndpointValuedModel(EndpointModel endpointModel, PropertyValueFactory propertyValueFactory) {
        this.fullPathWithParamBrackets = endpointModel.getFullPathWithParamBrackets();
        this.httpMethod = endpointModel.getHttpMethod();
        this.consumes = endpointModel.getConsumes();
        this.propertyValueNode = new PropertyValueNode(null, true);

        endpointModel.getSimpleParametersMap().entrySet().forEach(
            nameToPropertyMapEntry -> {
                this.propertyValueNode.getPropertiesMap().put(nameToPropertyMapEntry.getKey(),
                    convertPropertyToPropertyValue(nameToPropertyMapEntry.getValue(), propertyValueFactory));
            });

        endpointModel.getChildrenMap().entrySet().forEach(nameToPropertyNodeMap -> {
            this.propertyValueNode.getChildrenMap().put(
                nameToPropertyNodeMap.getKey(), new PropertyValueNode(nameToPropertyNodeMap.getValue(), propertyValueFactory));
        });

        this.httpResponseCodesCollection = endpointModel.getHttpResponseCodesList().stream()
            .map(httpResponseCode -> new Integer(httpResponseCode))
            .collect(Collectors.toList());
    }

    private EndpointValuedModel() {
        this.propertyValueNode = new PropertyValueNode(null, true);
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

    public PropertyValueNode getPropertyValueNode() {
        return propertyValueNode;
    }

    public void setPropertyValueNode(PropertyValueNode propertyValueNode) {
        this.propertyValueNode = propertyValueNode;
    }

    public Map<String, PropertyValue> getPropertiesMap() {
        return propertyValueNode.getPropertiesMap();
    }

    public Map<String, PropertyValueNode> getChildrenMap() {
        return propertyValueNode.getChildrenMap();
    }

    public String getTestComment() {
        return testComment;
    }

    public void setTestComment(String testComment) {
        this.testComment = testComment;
    }

    public Collection<Integer> getHttpResponseCodesCollection() {
        return httpResponseCodesCollection;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        EndpointValuedModel newEndpointValuedModel = new EndpointValuedModel();
        newEndpointValuedModel.fullPathWithParamBrackets = this.fullPathWithParamBrackets;
        newEndpointValuedModel.httpMethod = this.httpMethod;
        newEndpointValuedModel.consumes = new LinkedList<>(this.consumes);
        newEndpointValuedModel.propertyValueNode = new PropertyValueNode(this.propertyValueNode.getParameterLocation(), this.propertyValueNode.isRequired());

        // Keys are not cloned, as they are not meant to be changed
        for (Map.Entry<String, PropertyValue> entry : this.getPropertiesMap().entrySet()) {
            newEndpointValuedModel.propertyValueNode.getPropertiesMap().put(entry.getKey(), (PropertyValue) entry.getValue().clone());
        }

        for (Map.Entry<String, PropertyValueNode> entry : this.getChildrenMap().entrySet()) {
            newEndpointValuedModel.propertyValueNode.getChildrenMap().put(entry.getKey(), (PropertyValueNode) entry.getValue().clone());
        }

        newEndpointValuedModel.httpResponseCodesCollection = new LinkedList<>(this.httpResponseCodesCollection);
        return newEndpointValuedModel;
    }

    private PropertyValue convertPropertyToPropertyValue(Property property, PropertyValueFactory propertyValueFactory) {
        return propertyValueFactory.getPropertyValueFromProperty(property);
    }

    @Override
    public String toString() {
        return "EndpointValuedModel{"
            + "fullPathWithParamBrackets='" + fullPathWithParamBrackets + '\''
            + ", httpMethod=" + httpMethod
            + ", propertyValueNode=" + propertyValueNode
            + '}';
    }
}
