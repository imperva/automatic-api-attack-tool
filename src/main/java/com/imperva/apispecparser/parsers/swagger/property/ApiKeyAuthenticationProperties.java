package com.imperva.apispecparser.parsers.swagger.property;

public class ApiKeyAuthenticationProperties implements AuthenticationProperties {

    private final String name;

    private final ApiKeyAuthenticationParamType in;

    public ApiKeyAuthenticationProperties(String name, ApiKeyAuthenticationParamType in) {
        this.name = name;
        this.in = in;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ApiKeyAuthenticationParamType getIn() {
        return in;
    }
}
