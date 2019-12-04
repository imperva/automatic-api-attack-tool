package com.imperva.apiattacktool.model.valued;

import com.imperva.apiattacktool.fuzzing.Fuzzer;
import com.imperva.apiattacktool.model.tests.ParameterLocation;
import com.imperva.apispecparser.model.PropertyType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface PropertyValue<T> extends Cloneable {
    String getName();

    ParameterLocation getParameterLocation();

    PropertyType getType();

    Object clone() throws CloneNotSupportedException;

    T getValue();

    void setValue(T value);

    boolean isRequired();

    List<T> fuzz(Fuzzer fuzzer); // Visitor DP, accept method

    // https://docs.oracle.com/javase/tutorial/java/IandI/abstract.html
    // You want to specify the behavior of a particular data type, but not concerned about who implements its behavior
    // You want to take advantage of multiple inheritance of type
    default Map<String, Object> bodyParameterJsonRepresentationMap() {
        HashMap<String, Object> representationMap = new HashMap<>(1);
        representationMap.put(this.getName(), this.getValue());
        return representationMap;
    }

}
