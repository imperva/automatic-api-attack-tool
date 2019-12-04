package com.imperva.apispecparser.parsers.swagger.property;

import com.imperva.apiattacktool.model.tests.ParameterLocation;
import com.imperva.apispecparser.model.ArrayProperty;
import com.imperva.apispecparser.model.BooleanProperty;
import com.imperva.apispecparser.model.NumericProperty;
import com.imperva.apispecparser.model.Property;
import com.imperva.apispecparser.model.PropertyNode;
import com.imperva.apispecparser.model.StringProperty;
import com.imperva.apispecparser.parsers.swagger.propertynode.SwaggerArrayItemsToPropertyNode;
import com.imperva.apispecparser.parsers.swagger.propertynode.SwaggerPropertyNodeConverter;
import io.swagger.models.Model;
import io.swagger.models.parameters.AbstractSerializableParameter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SimpleSwaggerPropertyFactory {

    public static Property getPropertyFromAbstractSerializableParameter(AbstractSerializableParameter abstractSerializableParameter, String parameterName,
                                                                        Map<String, Model> definitions) {
        SwaggerParameterProperties swaggerParameterProperties = new SwaggerParameterProperties(abstractSerializableParameter, parameterName);
        return getPropertyFromSwaggerParameterProperties(swaggerParameterProperties, definitions);
    }

    public static Property getPropertyFromSwaggerProperty(io.swagger.models.properties.Property swaggerProperty, String propertyName,
                                                          ParameterLocation parameterLocation, boolean isRequired, Map<String, Model> definitions) {
        SwaggerParameterProperties swaggerParameterProperties = new SwaggerParameterProperties(swaggerProperty, propertyName, parameterLocation, isRequired);
        return getPropertyFromSwaggerParameterProperties(swaggerParameterProperties, definitions);
    }

    private static Property getPropertyFromSwaggerParameterProperties(SwaggerParameterProperties swaggerParameterProperties, Map<String, Model> definitions) {
        Property property = swaggerParameterProperties.getProperty();

        switch (property.getType()) {
            case ARRAY:
                // The propertyNode is created here, as it may come from both a parameter and a property.
                // We need to traverse the tree in both cases (and current logic doesn't support traversal for parameters)
                SwaggerPropertyNodeConverter swaggerPropertyNodeConverter = new SwaggerArrayItemsToPropertyNode(swaggerParameterProperties.getItems(),
                    property.isRequired(), property.getName(), property.getParameterLocation(), definitions);
                PropertyNode propertyNode = swaggerPropertyNodeConverter.getPropertyNode();
                return new ArrayProperty(property, swaggerParameterProperties.getUniqueItems(), propertyNode,
                    swaggerParameterProperties.getMaxItems(), swaggerParameterProperties.getMinItems(), swaggerParameterProperties.getCollectionFormat());
            case FLOAT:
                return new NumericProperty<>(property, getEnumAsType(Float.class, swaggerParameterProperties.getEnumList()),
                    swaggerParameterProperties.getMinimum(), swaggerParameterProperties.getMaximum(), swaggerParameterProperties.getMultipleOf(),
                    swaggerParameterProperties.getExclusiveMinimum(), swaggerParameterProperties.getExclusiveMaximum());
            case LONG:
                return new NumericProperty<>(property, getEnumAsType(Long.class, swaggerParameterProperties.getEnumList()),
                    swaggerParameterProperties.getMinimum(), swaggerParameterProperties.getMaximum(), swaggerParameterProperties.getMultipleOf(),
                    swaggerParameterProperties.getExclusiveMinimum(), swaggerParameterProperties.getExclusiveMaximum());
            case DECIMAL:
            case DOUBLE:
                return new NumericProperty<>(property, getEnumAsType(Double.class, swaggerParameterProperties.getEnumList()),
                    swaggerParameterProperties.getMinimum(), swaggerParameterProperties.getMaximum(), swaggerParameterProperties.getMultipleOf(),
                    swaggerParameterProperties.getExclusiveMinimum(), swaggerParameterProperties.getExclusiveMaximum());
            case BASE_INTEGER:
            case INTEGER:
                return new NumericProperty<>(property, getEnumAsType(Integer.class, swaggerParameterProperties.getEnumList()),
                    swaggerParameterProperties.getMinimum(), swaggerParameterProperties.getMaximum(), swaggerParameterProperties.getMultipleOf(),
                    swaggerParameterProperties.getExclusiveMinimum(), swaggerParameterProperties.getExclusiveMaximum());
            case UUID:
            case PASSWORD:
            case EMAIL:
            case DATETIME:
            case DATE:
                // As awkward this may sound. We should convert the (enum values) to Date using a dedicated active converter, instead of this circus
            case BINARY:
            case STRING:
                return new StringProperty(property, getEnumAsType(String.class, swaggerParameterProperties.getEnumList()),
                    swaggerParameterProperties.getMinLength(), swaggerParameterProperties.getMaxLength(), swaggerParameterProperties.getPattern());
            case BYTE_ARRAY:
                return new StringProperty(property, null,
                    swaggerParameterProperties.getMinLength(), swaggerParameterProperties.getMaxLength(), swaggerParameterProperties.getPattern());
            case BOOLEAN:
                return new BooleanProperty(property, getEnumAsType(Boolean.class, swaggerParameterProperties.getEnumList()));
            case UNTYPED:
            case FILE:
            default:
                return new Property(property);
        }
    }

    private static <T> List<T> getEnumAsType(Class<T> clazz, List<?> enumList) {
        if (enumList != null) {
            return enumList.stream().map(clazz::cast).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }
}
