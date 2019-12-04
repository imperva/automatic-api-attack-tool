package com.imperva.apispecparser.parsers.swagger.property;

import java.util.LinkedHashMap;
import java.util.Map;

public enum ApiKeyAuthenticationParamType {
    HEADER,
    QUERY;

    private static Map<String, ApiKeyAuthenticationParamType> names = new LinkedHashMap<>();

    public static ApiKeyAuthenticationParamType forValue(String value) {
        if (value == null) {
            return null;
        }

        return names.get(value.toLowerCase());
    }

    static {
        names.put("header", HEADER);
        names.put("query", QUERY);
    }
}
