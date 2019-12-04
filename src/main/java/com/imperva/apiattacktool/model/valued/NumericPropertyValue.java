package com.imperva.apiattacktool.model.valued;

import com.imperva.apiattacktool.fuzzing.Fuzzer;
import com.imperva.apispecparser.model.NumericProperty;
import com.imperva.apispecparser.model.Property;

import java.util.List;

public class NumericPropertyValue extends NumericProperty<Number> implements PropertyValue<Number> {

    private Number value;

    public NumericPropertyValue(NumericProperty<Number> numericProperty, Number value) {
        super(new Property(numericProperty.getParameterLocation(), numericProperty.getType(), numericProperty.getName(),
                numericProperty.isRequired(), numericProperty.getReadOnly(), numericProperty.getAllowEmptyValue()),
            numericProperty.getEnumList(), numericProperty.getMinimum(), numericProperty.getMaximum(), numericProperty.getMultipleOf(),
            numericProperty.getExclusiveMinimum(), numericProperty.getExclusiveMaximum());
        this.value = value;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public void setValue(Number value) {
        this.value = value;
    }

    @Override
    public Number getValue() {
        return value;
    }

    @Override
    public List<Number> fuzz(Fuzzer fuzzer) {
        return fuzzer.fuzz(this);
    }

    @Override
    public String toString() {
        return "NumericPropertyValue{"
            + "value=" + value
            + ", " + super.toString() + '}';
    }
}
