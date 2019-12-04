package com.imperva.apispecparser.model;

import com.imperva.apiattacktool.model.tests.ParameterLocation;

public class Property implements Cloneable {
    private ParameterLocation location;
    private PropertyType type;
    private String name;
    private boolean isRequired;

    /**
     * Relevant only for Schema "properties" definitions. Declares the property as "read only".
     * This means that it MAY be sent as part of a response but MUST NOT be sent as part of the request.
     */
    private boolean isReadOnly;

    /**
     * Valid only for either query or formData parameters and allows you to send a parameter with a name only or an empty value.
     * Default value is false
     */
    private boolean allowEmptyValue;

    public Property(ParameterLocation location, PropertyType type, String name, boolean isRequired, Boolean isReadOnly, Boolean allowEmptyValue) {
        this.location = location;
        this.type = type;
        this.name = name;
        this.isRequired = isRequired;
        this.isReadOnly = isReadOnly != null && isReadOnly;
        this.allowEmptyValue = allowEmptyValue != null && allowEmptyValue;
    }

    public Property(Property otherProperty) {
        this.location = otherProperty.getParameterLocation();
        this.type = otherProperty.getType();
        this.name = otherProperty.getName();
        this.isRequired = otherProperty.isRequired();
        this.isReadOnly = otherProperty.getReadOnly();
        this.allowEmptyValue = otherProperty.getAllowEmptyValue();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public ParameterLocation getParameterLocation() {
        return location;
    }

    public PropertyType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public boolean getReadOnly() {
        return isReadOnly;
    }

    public boolean getAllowEmptyValue() {
        return allowEmptyValue;
    }

    @Override
    public String toString() {
        return "{"
            + "location=" + location
            + ",type='" + type + '\''
            + ",name='" + name + '\''
            + ", isRequired='" + isRequired + '\''
            + '}';
    }
}
