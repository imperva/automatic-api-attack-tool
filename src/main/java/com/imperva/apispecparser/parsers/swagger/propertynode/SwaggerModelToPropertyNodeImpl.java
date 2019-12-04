package com.imperva.apispecparser.parsers.swagger.propertynode;

import com.imperva.apiattacktool.model.tests.ParameterLocation;
import io.swagger.models.Model;

import java.util.Map;

public abstract class SwaggerModelToPropertyNodeImpl<T extends Model> implements SwaggerPropertyNodeConverter {

    final T model;

    boolean isRequired;

    final String parentName;

    ParameterLocation parameterLocation;

    Map<String, Model> definitions;

    public SwaggerModelToPropertyNodeImpl(T model, boolean isRequired, String parentName, ParameterLocation parameterLocation, Map<String, Model> definitions) {
        this.model = model;
        this.isRequired = isRequired;
        this.parentName = parentName;
        this.parameterLocation = parameterLocation;
        this.definitions = definitions;
    }
}
