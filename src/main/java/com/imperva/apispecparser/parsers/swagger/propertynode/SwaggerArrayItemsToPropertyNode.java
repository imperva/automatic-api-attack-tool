package com.imperva.apispecparser.parsers.swagger.propertynode;

import com.imperva.apiattacktool.model.tests.ParameterLocation;
import com.imperva.apispecparser.model.PropertyNode;
import com.imperva.apispecparser.parsers.swagger.property.SimpleSwaggerPropertyFactory;
import io.swagger.models.Model;
import io.swagger.models.properties.ObjectProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;

import java.util.Map;

public class SwaggerArrayItemsToPropertyNode implements SwaggerPropertyNodeConverter {

    private Property property;
    private boolean isRequired;
    private final String parentName;
    private ParameterLocation parameterLocation;
    private Map<String, Model> definitions;


    public SwaggerArrayItemsToPropertyNode(Property property, boolean isRequired, String arrayPropertyName, ParameterLocation parameterLocation,
                                           Map<String, Model> definitions) {
        this.property = property;
        this.isRequired = isRequired;
        this.parentName = arrayPropertyName;
        this.parameterLocation = parameterLocation;
        this.definitions = definitions;
    }

    @Override
    public PropertyNode getPropertyNode() {
        if (property instanceof RefProperty) {
            RefProperty refProperty = (RefProperty) property;
            String ref = refProperty.getSimpleRef();
            Model model = definitions.get(ref);
            SwaggerPropertyNodeConverter swaggerPropertyNodeConverter =
                SwaggerModelToPropertyNodeFactory.get(model, property.getRequired(), parentName, parameterLocation, definitions);
            if (swaggerPropertyNodeConverter == null) {
                return null;
            }
            return swaggerPropertyNodeConverter.getPropertyNode();
        } else if (property instanceof ObjectProperty) {
            ObjectProperty objectProperty = (ObjectProperty) property;
            PropertyNode propertyNode = new PropertyNode(parentName, isRequired, parameterLocation);
            if (objectProperty.getProperties() != null) {
                objectProperty.getProperties().entrySet().forEach(propertyNameToProperty -> {
                    propertyNode.addPropertyValueType(propertyNameToProperty.getKey(),
                        SimpleSwaggerPropertyFactory.getPropertyFromSwaggerProperty(propertyNameToProperty.getValue(), parentName, parameterLocation,
                            isRequired, definitions));
                });
            }
        } else {
            PropertyNode propertyNode = new PropertyNode(parentName, isRequired, parameterLocation);
            propertyNode.addPropertyValueType(parentName, SimpleSwaggerPropertyFactory.getPropertyFromSwaggerProperty(property, parentName, parameterLocation,
                isRequired, definitions));
            return propertyNode;
        }

        return null;
    }
}
