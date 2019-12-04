package com.imperva.apispecparser.parsers.swagger.property;

import com.imperva.apiattacktool.model.tests.ParameterLocation;
import com.imperva.apispecparser.model.Property;
import com.imperva.apispecparser.model.PropertyType;
import io.swagger.models.parameters.AbstractSerializableParameter;
import io.swagger.models.properties.AbstractNumericProperty;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.DateProperty;
import io.swagger.models.properties.DateTimeProperty;
import io.swagger.models.properties.DoubleProperty;
import io.swagger.models.properties.FloatProperty;
import io.swagger.models.properties.IntegerProperty;
import io.swagger.models.properties.LongProperty;
import io.swagger.models.properties.UUIDProperty;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class SwaggerParameterProperties {
    private Property property;

    private List enumList;

    //Array Properties
    private io.swagger.models.properties.Property items;
    private String collectionFormat;
    private Boolean uniqueItems;
    private Integer minItems;
    private Integer maxItems;

    //Numeric Properties
    private BigDecimal minimum;
    private BigDecimal maximum;
    private Number multipleOf;
    private Boolean exclusiveMinimum;
    private Boolean exclusiveMaximum;

    //String properties
    private Integer minLength;
    private Integer maxLength;
    private String pattern;

    public SwaggerParameterProperties(AbstractSerializableParameter abstractSerializableParameter, String parameterName) {
        if (abstractSerializableParameter == null) {
            return;
        }

        PropertyType propertyType = PropertyType.getValueByTypeAndFormat(abstractSerializableParameter.getType(), abstractSerializableParameter.getFormat());
        if (propertyType == null) {
            return;
        }

        ParameterLocation parameterLocation = ParameterLocation.valueOf(abstractSerializableParameter.getIn().toUpperCase());

        this.property = new Property(parameterLocation, propertyType, parameterName, abstractSerializableParameter.getRequired(),
            abstractSerializableParameter.isReadOnly(), abstractSerializableParameter.getAllowEmptyValue());

        this.enumList = abstractSerializableParameter.getEnumValue();
        this.items = abstractSerializableParameter.getItems();
        this.collectionFormat = abstractSerializableParameter.getCollectionFormat();
        this.uniqueItems = abstractSerializableParameter.isUniqueItems();
        this.maxItems = abstractSerializableParameter.getMaxItems();
        this.minItems = abstractSerializableParameter.getMinItems();
        this.minimum = abstractSerializableParameter.getMinimum();
        this.maximum = abstractSerializableParameter.getMaximum();
        this.multipleOf = abstractSerializableParameter.getMultipleOf();
        this.exclusiveMinimum = abstractSerializableParameter.isExclusiveMinimum();
        this.exclusiveMaximum = abstractSerializableParameter.isExclusiveMaximum();
        this.minLength = abstractSerializableParameter.getMinLength();
        this.maxLength = abstractSerializableParameter.getMaxLength();
        this.pattern = abstractSerializableParameter.getPattern();
    }

    public SwaggerParameterProperties(io.swagger.models.properties.Property property, String propertyName, ParameterLocation parameterLocation,
                                      boolean isRequired) {
        if (property == null) {
            return;
        }

        PropertyType propertyType = PropertyType.getValueByTypeAndFormat(property.getType(), property.getFormat());
        if (propertyType == null) {
            return;
        }

        this.property = new Property(parameterLocation, propertyType, propertyName, isRequired, property.getReadOnly(),
            property.getAllowEmptyValue());

        this.enumList = getEnumStringList(property);

        if (property instanceof ArrayProperty) {
            ArrayProperty arrayProperty = (ArrayProperty) property;
            this.items = ((ArrayProperty) property).getItems();
            this.uniqueItems = arrayProperty.getUniqueItems();
            this.maxItems = arrayProperty.getMaxItems();
            this.minItems = arrayProperty.getMinItems();
        }

        if (property instanceof io.swagger.models.properties.StringProperty) {
            io.swagger.models.properties.StringProperty stringProperty = (io.swagger.models.properties.StringProperty) property;
            this.minLength = stringProperty.getMinLength();
            this.maxLength = stringProperty.getMaxLength();
            this.pattern = stringProperty.getPattern();
        }

        if (property instanceof AbstractNumericProperty) {
            AbstractNumericProperty abstractNumericProperty = (AbstractNumericProperty) property;
            this.minimum = abstractNumericProperty.getMinimum();
            this.maximum = abstractNumericProperty.getMaximum();
            this.multipleOf = abstractNumericProperty.getMultipleOf();
            this.exclusiveMinimum = abstractNumericProperty.getExclusiveMinimum();
            this.exclusiveMaximum = abstractNumericProperty.getExclusiveMaximum();
        }
    }

    private static List getEnumStringList(io.swagger.models.properties.Property property) {
        // We love swagger parser
        if (property instanceof io.swagger.models.properties.ArrayProperty) {
            io.swagger.models.properties.ArrayProperty arrayProperty = (io.swagger.models.properties.ArrayProperty) property;
            return getEnumStringList(arrayProperty.getItems());
        }
        if (property instanceof io.swagger.models.properties.StringProperty) {
            io.swagger.models.properties.StringProperty stringProperty = (io.swagger.models.properties.StringProperty) property;
            if (stringProperty.getEnum() == null) {
                return null;
            }
            return stringProperty.getEnum();
        }
        if (property instanceof io.swagger.models.properties.BooleanProperty) {
            io.swagger.models.properties.BooleanProperty booleanProperty = (io.swagger.models.properties.BooleanProperty) property;
            if (booleanProperty.getEnum() == null) {
                return null;
            }
            return booleanProperty.getEnum().stream().map(String::valueOf).collect(Collectors.toList());
        }
        if (property instanceof IntegerProperty) {
            IntegerProperty integerProperty = (IntegerProperty) property;
            return integerProperty.getEnum();
        }
        if (property instanceof LongProperty) {
            LongProperty longProperty = (LongProperty) property;
            if (longProperty.getEnum() == null) {
                return null;
            }
            return longProperty.getEnum().stream().map(String::valueOf).collect(Collectors.toList());
        }
        if (property instanceof DoubleProperty) {
            DoubleProperty doubleProperty = (DoubleProperty) property;
            if (doubleProperty.getEnum() == null) {
                return null;
            }
            return doubleProperty.getEnum().stream().map(String::valueOf).collect(Collectors.toList());
        }
        if (property instanceof FloatProperty) {
            FloatProperty floatProperty = (FloatProperty) property;
            if (floatProperty.getEnum() == null) {
                return null;
            }
            return floatProperty.getEnum().stream().map(String::valueOf).collect(Collectors.toList());
        }
        if (property instanceof DateProperty) {
            DateProperty dateProperty = (DateProperty) property;
            return dateProperty.getEnum();
        }
        if (property instanceof DateTimeProperty) {
            DateTimeProperty dateTimeProperty = (DateTimeProperty) property;
            return dateTimeProperty.getEnum();
        }
        if (property instanceof UUIDProperty) {
            UUIDProperty uuidProperty = (UUIDProperty) property;
            return uuidProperty.getEnum();
        }
        return null;
    }

    public Property getProperty() {
        return property;
    }

    public List getEnumList() {
        return enumList;
    }

    public Boolean getUniqueItems() {
        return uniqueItems;
    }

    public Integer getMinItems() {
        return minItems;
    }

    public Integer getMaxItems() {
        return maxItems;
    }

    public io.swagger.models.properties.Property getItems() {
        return items;
    }

    public String getCollectionFormat() {
        return collectionFormat;
    }

    public BigDecimal getMinimum() {
        return minimum;
    }

    public BigDecimal getMaximum() {
        return maximum;
    }

    public Number getMultipleOf() {
        return multipleOf;
    }

    public Boolean getExclusiveMinimum() {
        return exclusiveMinimum;
    }

    public Boolean getExclusiveMaximum() {
        return exclusiveMaximum;
    }

    public Integer getMinLength() {
        return minLength;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public String getPattern() {
        return pattern;
    }
}
