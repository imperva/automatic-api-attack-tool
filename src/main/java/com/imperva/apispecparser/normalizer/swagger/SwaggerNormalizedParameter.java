package com.imperva.apispecparser.normalizer.swagger;

import com.imperva.apispecparser.normalizer.NormalizedParameter;
import io.swagger.models.Model;
import io.swagger.models.parameters.Parameter;

import java.util.HashMap;
import java.util.Map;

public class SwaggerNormalizedParameter<T extends Parameter> implements NormalizedParameter<T> {

    private final T parameter;

    private SwaggerDefinitions swaggerDefinitions;

    private int totalPropertiesCount;

    public SwaggerNormalizedParameter(T parameter) {
        this(parameter, new HashMap<>(), 0);
    }

    public SwaggerNormalizedParameter(T parameter, Map<String, Model> normalizedDefinitions, int totalPropertiesCount) {
        this.parameter = parameter;
        this.swaggerDefinitions = new SwaggerDefinitions(normalizedDefinitions);
        this.totalPropertiesCount = totalPropertiesCount;
    }

    void addDefinition(String modelName, Model model) {
        swaggerDefinitions.addDefinition(modelName, model);
    }

    void removeDefinition(String modelName) {
        swaggerDefinitions.removeDefinition(modelName);
    }

    Model getDefinition(String modelName) {
        return swaggerDefinitions.getDefinition(modelName);
    }

    boolean containsDefinition(String modelName) {
        return swaggerDefinitions.containsDefinition(modelName);
    }

    void incrementTotalPropertiesCount() {
        totalPropertiesCount += 1;
    }

    @Override
    public SwaggerDefinitions getApiDefinitions() {
        return swaggerDefinitions;
    }

    public int getTotalPropertiesCount() {
        return totalPropertiesCount;
    }

    public T getParameter() {
        return parameter;
    }
}
