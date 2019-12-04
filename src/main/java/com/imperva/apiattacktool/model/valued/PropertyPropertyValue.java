package com.imperva.apiattacktool.model.valued;

import com.imperva.apiattacktool.fuzzing.Fuzzer;
import com.imperva.apispecparser.model.Property;

import java.util.List;

public class PropertyPropertyValue extends Property implements PropertyValue<String> {

    private String value;

    public PropertyPropertyValue(Property property, String value) {
        super(property);
        this.value = value;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
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
        return "PropertyPropertyValue{"
            + "value='" + value + '\''
            + ", Property=" + super.toString() + '}';
    }
}
