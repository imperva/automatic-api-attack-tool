package com.imperva.apispecparser.parsers.swagger.propertynode;

import com.imperva.apiattacktool.model.tests.ParameterLocation;
import com.imperva.apispecparser.model.PropertyNode;
import io.swagger.models.Model;
import io.swagger.models.RefModel;

import java.util.Map;

public class SwaggerRefModelToPropertyNode extends SwaggerModelToPropertyNodeImpl<RefModel> {

    public SwaggerRefModelToPropertyNode(RefModel model, boolean isRequired, String parentName, ParameterLocation parameterLocation,
                                         Map<String, Model> definitions) {
        super(model, isRequired, parentName, parameterLocation, definitions);
    }

    @Override
    public PropertyNode getPropertyNode() {
        Model model = definitions.get(this.model.getSimpleRef());

        //Prevent endless loop when Ref model points to itself
        if (model == this.model) {
            return null;
        }

        SwaggerPropertyNodeConverter swaggerPropertyNodeConverter = SwaggerModelToPropertyNodeFactory.get(model, isRequired, parentName, parameterLocation,
            definitions);
        return swaggerPropertyNodeConverter == null ? null : swaggerPropertyNodeConverter.getPropertyNode();
    }
}
