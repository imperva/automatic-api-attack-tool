package com.imperva.apispecparser.model;

import java.math.BigDecimal;
import java.util.List;

public class NumericProperty<T extends Number> extends EnumerableProperty<T> {
    private BigDecimal minimum;
    private BigDecimal maximum;
    private Number multipleOf;
    private boolean exclusiveMinimum;
    private boolean exclusiveMaximum;

    public NumericProperty(Property property, List<T> enumList,
                           BigDecimal minimum, BigDecimal maximum, Number multipleOf, Boolean exclusiveMinimum,
                           Boolean exclusiveMaximum) {
        super(property, enumList);
        this.minimum = minimum;
        this.maximum = maximum;
        this.multipleOf = multipleOf;
        this.exclusiveMinimum = exclusiveMinimum != null && exclusiveMinimum;
        this.exclusiveMaximum = exclusiveMaximum != null && exclusiveMaximum;
    }

    public BigDecimal getMinimum() {
        return minimum;
    }

    public BigDecimal getMaximum() {
        return maximum;
    }

    public Number getMultipleOf() {
        return multipleOf;
    }

    public boolean getExclusiveMinimum() {
        return exclusiveMinimum;
    }

    public boolean getExclusiveMaximum() {
        return exclusiveMaximum;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return "NumericProperty{"
            + "minimum=" + minimum
            + ", maximum=" + maximum
            + ", multipleOf=" + multipleOf
            + ", exclusiveMinimum=" + exclusiveMinimum
            + ", exclusiveMaximum=" + exclusiveMaximum
            + ", EnumerableProperty=" + super.toString() + '}';
    }
}
