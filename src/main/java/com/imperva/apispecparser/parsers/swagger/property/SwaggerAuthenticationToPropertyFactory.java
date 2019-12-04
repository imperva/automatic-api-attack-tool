package com.imperva.apispecparser.parsers.swagger.property;

import com.imperva.apiattacktool.model.tests.ParameterLocation;
import com.imperva.apispecparser.model.Property;
import com.imperva.apispecparser.model.PropertyType;
import com.imperva.apispecparser.model.StringProperty;
import io.swagger.models.auth.ApiKeyAuthDefinition;
import io.swagger.models.auth.BasicAuthDefinition;
import io.swagger.models.auth.In;
import io.swagger.models.auth.SecuritySchemeDefinition;

public class SwaggerAuthenticationToPropertyFactory {

    public static Property get(SecuritySchemeDefinition securitySchemeDefinition) {
        Property property = null;

        if (securitySchemeDefinition instanceof BasicAuthDefinition) {
            property = new Property(ParameterLocation.HEADER, PropertyType.STRING, "Authorization", true, false, false);
        } else if (securitySchemeDefinition instanceof ApiKeyAuthDefinition) {
            ApiKeyAuthDefinition apiKeyAuthDefinition = (ApiKeyAuthDefinition) securitySchemeDefinition;

            In in = apiKeyAuthDefinition.getIn();
            AuthenticationProperties authenticationProperties = null;

            if (in != null && in.toValue() != null) {
                authenticationProperties = new ApiKeyAuthenticationProperties(apiKeyAuthDefinition.getName(),
                    ApiKeyAuthenticationParamType.forValue(in.toValue()));
            }

            if (in != null && authenticationProperties != null) {
                switch (in) {
                    case QUERY:
                        property = new Property(ParameterLocation.QUERY, PropertyType.STRING, authenticationProperties.getName(), true, false, false);
                        break;
                    case HEADER:
                        property = new Property(ParameterLocation.HEADER, PropertyType.STRING, authenticationProperties.getName(), true, false, false);
                        break;
                    default:
                        property = null;
                        break;
                }
            }
        }

        return property == null ? null : new StringProperty(property, null, null, null, null);
    }
}
