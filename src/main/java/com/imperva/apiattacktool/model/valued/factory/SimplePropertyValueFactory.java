package com.imperva.apiattacktool.model.valued.factory;

import com.imperva.apiattacktool.model.valued.ArrayPropertyValue;
import com.imperva.apiattacktool.model.valued.BooleanPropertyValue;
import com.imperva.apiattacktool.model.valued.NumericPropertyValue;
import com.imperva.apiattacktool.model.valued.PropertyPropertyValue;
import com.imperva.apiattacktool.model.valued.PropertyValue;
import com.imperva.apiattacktool.model.valued.StringPropertyValue;
import com.imperva.apispecparser.model.ArrayProperty;
import com.imperva.apispecparser.model.BooleanProperty;
import com.imperva.apispecparser.model.NumericProperty;
import com.imperva.apispecparser.model.Property;
import com.imperva.apispecparser.model.StringProperty;

public class SimplePropertyValueFactory implements PropertyValueFactory {

    @Override
    public PropertyValue getPropertyValueFromProperty(Property property) {
        if (property == null) {
            return null;
        }

        switch (property.getType()) {
            case ARRAY:
                ArrayProperty arrayProperty = (ArrayProperty) property;
                return new ArrayPropertyValue(arrayProperty, null);
            case FLOAT:
            case LONG:
            case DECIMAL:
            case DOUBLE:
            case BASE_INTEGER:
            case INTEGER:
                NumericProperty numericProperty = (NumericProperty) property;
                return new NumericPropertyValue(numericProperty, null);
            case UUID:
            case PASSWORD:
            case EMAIL:
            case DATETIME:
            case DATE:
                // As awkward this may sound. We should convert the (enum values) to Date using a dedicated active propertynode, instead of this circus
            case BINARY:
            case STRING:
            case BYTE_ARRAY:
                StringProperty stringProperty = (StringProperty) property;
                return new StringPropertyValue(stringProperty, null);
            case BOOLEAN:
                BooleanProperty booleanProperty = (BooleanProperty) property;
                return new BooleanPropertyValue(booleanProperty, null);
            case UNTYPED:
            case FILE:
            default:
                return new PropertyPropertyValue(property, null);
        }
    }
}
