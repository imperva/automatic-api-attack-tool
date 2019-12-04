package com.imperva.apispecparser.model;

import com.imperva.apiattacktool.infra.Tuple;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum PropertyType {
    ARRAY("array", "null", null),
    BOOLEAN("boolean", "null", Boolean.class),
    BASE_INTEGER("integer", "null", Integer.class),
    INTEGER("integer", "int32", Integer.class),
    LONG("integer", "int64", Long.class),
    DECIMAL("number", "null", Double.class), // This also accepts custom formats
    DOUBLE("number", "double", Double.class),
    FLOAT("number", "float", Float.class),
    BINARY("string", "binary", String.class),
    BYTE_ARRAY("string", "byte", String.class),
    DATE("string", "date", String.class),
    DATETIME("string", "date-time", String.class),
    EMAIL("string", "email", String.class),
    PASSWORD("string", "password", String.class),
    UUID("string", "uuid", String.class),
    STRING("string", "null", String.class),
    FILE("file", "null", null),
    UNTYPED("null", "null", null);

    private String type;
    private String format;
    private Class castingClass;
    private static final Map<Tuple<String, String>, PropertyType> enumValuesMap = Collections.unmodifiableMap(initializeMapping());

    PropertyType(String type, String format, Class clazz) {
        this.type = type;
        this.format = format;
        this.castingClass = clazz;
    }

    public static PropertyType getValueByTypeAndFormat(String type, String format) {
        if (type == null) {
            type = "null";
        }
        if (format == null) {
            format = "null";
        }

        if ("number".equals(type) && !"double".equals(format) && !"float".equals(format)) {
            return DECIMAL;
        }
        return enumValuesMap.get(new Tuple<>(type, format));
    }

    private static Map<Tuple<String, String>, PropertyType> initializeMapping() {
        Map<Tuple<String, String>, PropertyType> enumValuesMap = new HashMap<>();
        for (PropertyType propertyType : PropertyType.values()) {
            enumValuesMap.put(new Tuple<>(propertyType.type, propertyType.format), propertyType);
        }
        return enumValuesMap;
    }

    public Class getCastingClass() {
        return castingClass;
    }
}
