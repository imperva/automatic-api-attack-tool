package com.imperva.apispecparser.normalizer.swagger;

import com.imperva.apispecparser.normalizer.ApiDefinitions;
import io.swagger.models.Model;

import java.util.HashMap;
import java.util.Map;

public class SwaggerDefinitions implements ApiDefinitions {

    private Map<String, Model> definitions;

    public SwaggerDefinitions(Map<String, Model> definitions) {
        this.definitions = definitions;
    }

    @Override
    public Map<String, Model> getDefinitions() {
        return definitions;
    }

    public void addDefinition(String modelName, Model model) {
        if (definitions == null) {
            definitions = new HashMap<>();
        }

        definitions.put(getSimpleModelName(modelName), model);
    }

    public void removeDefinition(String modelName) {
        if (definitions != null) {
            definitions.remove(modelName);
        }
    }

    public Model getDefinition(String modelName) {
        if (definitions == null || modelName == null) {
            return null;
        }

        return definitions.get(getSimpleModelName(modelName));
    }

    public boolean containsDefinition(String modelName) {
        return definitions != null && definitions.get(modelName) != null;
    }

    private String getSimpleModelName(String modelName) {
        return modelName == null ? null : modelName.substring(modelName.lastIndexOf("/") + 1);
    }
}
