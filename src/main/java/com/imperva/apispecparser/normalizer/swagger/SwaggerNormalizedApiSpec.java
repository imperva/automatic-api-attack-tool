package com.imperva.apispecparser.normalizer.swagger;

import com.imperva.apispecparser.normalizer.NormalizedApiSpecImpl;
import com.imperva.apispecparser.normalizer.NormalizedPath;
import io.swagger.models.Path;

public class SwaggerNormalizedApiSpec extends NormalizedApiSpecImpl {

    public void addNormalizedPath(Path path, SwaggerNormalizedPath swaggerNormalizedPath) {
        if (path != null) {
            addNormalizedPath(path.hashCode(), swaggerNormalizedPath);
        }
    }

    public SwaggerNormalizedPath getNormalizedPath(Path path) {
        if (path != null) {
            return getNormalizedPath(path.hashCode());
        }

        return null;
    }

    @Override
    public SwaggerNormalizedPath getNormalizedPath(Integer pathHashCode) {
        NormalizedPath normalizedPath = super.getNormalizedPath(pathHashCode);
        return normalizedPath == null ? null : (SwaggerNormalizedPath) normalizedPath;
    }
}
