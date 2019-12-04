package com.imperva.apispecparser.parsers.swagger.property;

import com.imperva.apispecparser.model.Property;
import io.swagger.models.parameters.AbstractSerializableParameter;

public interface SwaggerPropertyFactory {
    Property getPropertyFromAbstractSerializableParameter(AbstractSerializableParameter abstractSerializableParameter, String parameterName);
}
