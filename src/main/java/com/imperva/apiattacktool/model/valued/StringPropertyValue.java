package com.imperva.apiattacktool.model.valued;

import com.imperva.apiattacktool.fuzzing.Fuzzer;
import com.imperva.apispecparser.model.Property;
import com.imperva.apispecparser.model.StringProperty;

import java.util.List;

public class StringPropertyValue extends StringProperty implements PropertyValue<String> {

    private String value;

    public StringPropertyValue(StringProperty stringProperty, String value) {
        super(new Property(stringProperty.getParameterLocation(), stringProperty.getType(), stringProperty.getName(), stringProperty.isRequired(),
                stringProperty.getReadOnly(), stringProperty.getAllowEmptyValue()),
            stringProperty.getEnumList(), stringProperty.getMinLength(), stringProperty.getMaxLength(), stringProperty.getPattern());
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
        return "StringPropertyValue{"
            + "value='" + value + '\''
            + ", " + super.toString() + '}';
    }
}
