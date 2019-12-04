package com.imperva.apispecparser.normalizer.swagger;

import com.imperva.apispecparser.normalizer.NormalizedEndpoint;
import com.imperva.apispecparser.normalizer.NormalizedPathImpl;
import io.swagger.models.Operation;
import io.swagger.models.Path;

public class SwaggerNormalizedPath extends NormalizedPathImpl {

    private final Path path;

    public SwaggerNormalizedPath(Path path) {
        this.path = path;
    }

    public void addNormalizedEndpoint(Operation operation, SwaggerNormalizedEndpoint swaggerNormalizedEndpoint) {
        if (path != null && operation != null) {
            addNormalizedEndpoint(operation.hashCode(), swaggerNormalizedEndpoint);
        }
    }

    public SwaggerNormalizedEndpoint getNormalizedEndpoint(Operation operation) {
        if (operation == null) {
            return null;
        }

        NormalizedEndpoint normalizedEndpoint = super.getNormalizedEndpoint((operation.hashCode()));
        return normalizedEndpoint == null ? null : (SwaggerNormalizedEndpoint) normalizedEndpoint;
    }
}
