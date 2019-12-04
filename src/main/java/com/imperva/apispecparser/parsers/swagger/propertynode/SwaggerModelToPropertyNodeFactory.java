package com.imperva.apispecparser.parsers.swagger.propertynode;

import com.imperva.apiattacktool.model.tests.ParameterLocation;
import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.RefModel;

import java.util.Map;

public class SwaggerModelToPropertyNodeFactory {

    public static SwaggerPropertyNodeConverter get(Model model, boolean isRequired, String parentName, ParameterLocation parameterLocation,
                                                   Map<String, Model> definitions) {
        if (model == null) {
            return null;
        }

        if (model instanceof ModelImpl) {
            return new SwaggerModelImplToPropertyNode((ModelImpl) model, isRequired, parentName, parameterLocation, definitions);
        } else if (model instanceof RefModel) {
            return new SwaggerRefModelToPropertyNode((RefModel) model, isRequired, parentName, parameterLocation, definitions);
        } else {
            return null;
        }
    }
}
