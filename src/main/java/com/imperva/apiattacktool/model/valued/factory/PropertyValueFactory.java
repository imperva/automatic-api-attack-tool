package com.imperva.apiattacktool.model.valued.factory;

import com.imperva.apiattacktool.model.valued.PropertyValue;
import com.imperva.apispecparser.model.Property;

public interface PropertyValueFactory {
    PropertyValue getPropertyValueFromProperty(Property property);
}
