package com.imperva.apispecparser.model;

import java.util.List;

public class BooleanProperty extends EnumerableProperty<Boolean> {
    public BooleanProperty(Property property, List<Boolean> enumList) {
        super(property, enumList);
    }
}
