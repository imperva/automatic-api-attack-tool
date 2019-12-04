package com.imperva.apiattacktool.model.valued;

import com.imperva.apiattacktool.fuzzing.Fuzzer;
import com.imperva.apispecparser.model.ArrayProperty;
import com.imperva.apispecparser.model.Property;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ArrayPropertyValue extends ArrayProperty implements PropertyValue<List<PropertyValueNode>> {

    private List<PropertyValueNode> value;


    public ArrayPropertyValue(ArrayProperty arrayProperty, List<PropertyValueNode> items) {
        super(new Property(arrayProperty.getParameterLocation(), arrayProperty.getType(), arrayProperty.getName(), arrayProperty.isRequired(),
                arrayProperty.getReadOnly(), arrayProperty.getAllowEmptyValue()),
            arrayProperty.getAreItemsUnique(), arrayProperty.getItems(), arrayProperty.getMinimumItems(), arrayProperty.getMaximumItems(),
            arrayProperty.getCollectionFormat().getFormatString());
        this.value = items;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public void setValue(List<PropertyValueNode> value) {
        this.value = value;
    }

    @Override
    public List<PropertyValueNode> getValue() {
        return value;
    }

    @Override
    public List<List<PropertyValueNode>> fuzz(Fuzzer fuzzer) {
        return fuzzer.fuzz(this);
    }

    @Override
    public Map<String, Object> bodyParameterJsonRepresentationMap() {
        HashMap<String, Object> representationMap = new HashMap<>(1);
        representationMap.put(this.getName(), value.stream().map(PropertyValueNode::bodyParameterJsonRepresentationMap).collect(Collectors.toList()));
        return representationMap;
    }

    @Override
    public String toString() {
        return "ArrayPropertyValue{"
            + "value=" + value
            + ", " + super.toString() + '}';
    }
}
