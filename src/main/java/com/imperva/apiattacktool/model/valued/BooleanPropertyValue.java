package com.imperva.apiattacktool.model.valued;

import com.imperva.apiattacktool.fuzzing.Fuzzer;
import com.imperva.apispecparser.model.BooleanProperty;
import com.imperva.apispecparser.model.Property;

import java.util.List;

public class BooleanPropertyValue extends BooleanProperty implements PropertyValue<String> {

    String value;

    public BooleanPropertyValue(BooleanProperty booleanProperty, String value) {
        super(new Property(booleanProperty.getParameterLocation(), booleanProperty.getType(), booleanProperty.getName(),
                booleanProperty.isRequired(), booleanProperty.getReadOnly(), booleanProperty.getAllowEmptyValue()),
            booleanProperty.getEnumList());
        this.value = value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public List<String> fuzz(Fuzzer fuzzer) {
        return fuzzer.fuzz(this);
    }

    @Override
    public String toString() {
        return "BooleanPropertyValue{"
            + "value='" + value + '\''
            + ", " + super.toString() + '}';
    }
}
