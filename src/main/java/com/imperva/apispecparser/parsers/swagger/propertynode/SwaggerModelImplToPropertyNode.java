package com.imperva.apispecparser.parsers.swagger.propertynode;

import com.imperva.apiattacktool.model.tests.ParameterLocation;
import com.imperva.apispecparser.model.PropertyNode;
import com.imperva.apispecparser.parsers.swagger.property.SimpleSwaggerPropertyFactory;
import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;

import java.util.Map;

public class SwaggerModelImplToPropertyNode extends SwaggerModelToPropertyNodeImpl<ModelImpl> {

    public SwaggerModelImplToPropertyNode(ModelImpl model, boolean isRequired, String parentName, ParameterLocation parameterLocation,
                                          Map<String, Model> definitions) {
        super(model, isRequired, parentName, parameterLocation, definitions);
    }


    @Override
    public PropertyNode getPropertyNode() {
        PropertyNode propertyNode = new PropertyNode(parentName, isRequired, parameterLocation);

        Map<String, Property> propertyMap = model.getProperties();

        if (propertyMap == null) {
            return propertyNode;
        }

        for (Map.Entry<String, Property> entry : propertyMap.entrySet()) {
            Property property = entry.getValue();

            if (property instanceof RefProperty) {
                RefProperty refProperty = (RefProperty) property;
                String ref = refProperty.getSimpleRef();
                Model model = definitions.get(ref);

                SwaggerPropertyNodeConverter swaggerPropertyNodeConverter = SwaggerModelToPropertyNodeFactory.get(model, property.getRequired(), parentName,
                    parameterLocation, definitions);

                if (swaggerPropertyNodeConverter == null) {
                    return null;
                }

                propertyNode.addPropertyNode(entry.getKey(), swaggerPropertyNodeConverter.getPropertyNode());
            } else {
                propertyNode.addPropertyValueType(entry.getKey(), SimpleSwaggerPropertyFactory.getPropertyFromSwaggerProperty(property, entry.getKey(),
                    parameterLocation, isPropertyRequired(entry.getKey()), definitions));
            }
        }

        return propertyNode;
    }

    private boolean isPropertyRequired(String propertyName) {
        return model.getRequired() != null && model.getRequired().contains(propertyName);
    }
}
