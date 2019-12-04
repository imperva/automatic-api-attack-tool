package com.imperva.apispecparser.normalizer.swagger;

import com.imperva.apispecparser.normalizer.NormalizedEndpointImpl;
import com.imperva.apispecparser.normalizer.NormalizedParameter;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.parameters.Parameter;

public class SwaggerNormalizedEndpoint extends NormalizedEndpointImpl {

    private final Path path;
    private final Operation operation;

    public SwaggerNormalizedEndpoint(String endpointName, Path path, Operation operation) {
        super(endpointName);
        this.path = path;
        this.operation = operation;
    }

    public void addNormalizedParameter(Parameter parameter, SwaggerNormalizedParameter normalizedParameter) {
        if (path != null && operation != null && parameter != null) {
            addNormalizedParameter(parameter.hashCode(), normalizedParameter);
        }
    }

    public SwaggerNormalizedParameter getNormalizedParameter(Parameter parameter) {
        if (parameter == null) {
            return null;
        }

        NormalizedParameter normalizedParameter = super.getNormalizedParameter(parameter.hashCode());
        return normalizedParameter == null ? null : (SwaggerNormalizedParameter) normalizedParameter;
    }
}
