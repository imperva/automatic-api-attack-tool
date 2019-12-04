package com.imperva.apispecparser.model;

import java.util.List;

public class StringProperty extends EnumerableProperty<String> {
    private int minLength;
    private Integer maxLength;
    private String pattern;

    public StringProperty(Property property, List<String> enumList,
                          Integer minLength, Integer maxLength, String pattern) {
        super(property, enumList);
        this.minLength = minLength == null ? 0 : minLength;
        this.maxLength = maxLength;
        this.pattern = pattern;
    }

    public int getMinLength() {
        return minLength;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public String getPattern() {
        return pattern;
    }

    @Override
    public String toString() {
        return "StringProperty{"
            + "minLength=" + minLength
            + ", maxLength=" + maxLength
            + ", pattern='" + pattern + '\''
            + ", EnumerableProperty= " + super.toString() + '}';
    }
}
