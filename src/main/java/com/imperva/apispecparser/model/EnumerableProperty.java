package com.imperva.apispecparser.model;

import java.util.List;

public class EnumerableProperty<T> extends Property {

    private List<T> enumList;

    public EnumerableProperty(Property property, List<T> enumList) {
        super(property);
        this.enumList = enumList;
    }

    public List<T> getEnumList() {
        return enumList;
    }

    @Override
    public String toString() {
        return "{"
            + "enumList=" + enumList
            + ", Property=" + super.toString() + '}';
    }
}
