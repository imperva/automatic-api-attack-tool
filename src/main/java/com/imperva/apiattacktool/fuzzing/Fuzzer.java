package com.imperva.apiattacktool.fuzzing;

import com.imperva.apiattacktool.model.valued.PropertyValueNode;
import com.imperva.apispecparser.model.ArrayProperty;
import com.imperva.apispecparser.model.BooleanProperty;
import com.imperva.apispecparser.model.NumericProperty;
import com.imperva.apispecparser.model.Property;
import com.imperva.apispecparser.model.StringProperty;

import java.util.List;

public interface Fuzzer {
    List<String> fuzz(Property property);

    List<String> fuzz(BooleanProperty booleanProperty);

    List<String> fuzz(StringProperty stringProperty);

    <T extends Number> List<Number> fuzz(NumericProperty<T> numericProperty);

    List<List<PropertyValueNode>> fuzz(ArrayProperty arrayProperty);
}
