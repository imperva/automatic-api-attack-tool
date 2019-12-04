package com.imperva.apiattacktool.model.valued;

import com.imperva.apiattacktool.fuzzing.Fuzzer;
import com.imperva.apiattacktool.model.tests.ParameterLocation;
import com.imperva.apispecparser.model.PropertyType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * An adapter to adapt getValue to return a string, whereas the array items property is represented by PropertyValueNode
 * It should be used in the values translation phase
 */
public class ArrayPropertyValueAdapter implements PropertyValue<String> {

    private ArrayPropertyValue arrayPropertyValue;

    public ArrayPropertyValueAdapter(ArrayPropertyValue arrayPropertyValue) {
        this.arrayPropertyValue = arrayPropertyValue;
    }

    @Override
    public String getName() {
        return arrayPropertyValue.getName();
    }

    @Override
    public ParameterLocation getParameterLocation() {
        return arrayPropertyValue.getParameterLocation();
    }

    @Override
    public PropertyType getType() {
        return arrayPropertyValue.getType();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return arrayPropertyValue.clone();
    }

    @Override
    public String getValue() {
        List<PropertyValueNode> propertyValueNodeList = arrayPropertyValue.getValue();
        if (propertyValueNodeList != null && !propertyValueNodeList.isEmpty()) {
            String delimiterSeparatedArrayValues =
                propertyValueNodeList.stream()
                    .map(propertyValueNode ->
                        String.valueOf(
                            propertyValueNode.getPropertiesMap().entrySet().stream().findFirst().get().getValue().getValue()))
                    .collect(Collectors.joining(arrayPropertyValue.getCollectionFormat().getOutputString()));
            return delimiterSeparatedArrayValues;
        }
        return "";
    }

    @Override
    public void setValue(String value) {
        throw new IllegalStateException();
    }

    @Override
    public boolean isRequired() {
        return arrayPropertyValue.isRequired();
    }

    @Override
    public List<String> fuzz(Fuzzer fuzzer) {
        throw new IllegalStateException();
    }

    @Override
    public Map<String, Object> bodyParameterJsonRepresentationMap() {
        return arrayPropertyValue.bodyParameterJsonRepresentationMap();
    }
}
