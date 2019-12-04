package com.imperva.apiattacktool.fuzzing.value;

import com.imperva.apiattacktool.fuzzing.Fuzzer;
import com.imperva.apiattacktool.model.valued.PropertyValue;
import com.imperva.apiattacktool.model.valued.PropertyValueNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CommonValueFuzzer {
    private static final Logger logger = LoggerFactory.getLogger(CommonValueFuzzer.class);

    /**
     * @param propertyValueNode
     * @param fuzzer
     * @return a cloned instance with values filled in
     */
    protected PropertyValueNode traverseFuzzAndInjectPropertyValueNode(
        PropertyValueNode propertyValueNode, Fuzzer fuzzer) {
        PropertyValueNode clonedPropertyValueNode;
        try {
            clonedPropertyValueNode = (PropertyValueNode) propertyValueNode.clone();
        } catch (CloneNotSupportedException cloneNotSupportedException) {
            logger.error("Could not clone propertyValueNode: {} %n NOT GENERATING VALUE", propertyValueNode);
            return null;
        }

        if (clonedPropertyValueNode.getPropertiesMap().size() > 0) {
            fuzzAndInjectPropertyValues(clonedPropertyValueNode.getPropertiesMap(), fuzzer);
        }

        if (clonedPropertyValueNode.hasChildren()) {
            clonedPropertyValueNode.getChildrenMap().entrySet().forEach(
                stringToPropertyNodeMap ->
                    traverseFuzzAndInjectPropertyValueNode(stringToPropertyNodeMap.getValue(), fuzzer)
            );
        }
        return clonedPropertyValueNode;
    }

    // Make this something that is taken from a fuzzedModelGenerator
    // Take the generator in c'tor of the fuzzer and use it here to traverse the arrays' propertyValueNode
    protected void fuzzAndInjectPropertyValues(Map<String, PropertyValue> nameToPropertyValueMap, Fuzzer fuzzer) {
        List<String> propertiesToDeleteList = new LinkedList<>();
        nameToPropertyValueMap.entrySet().forEach(
            stringToPropertyValueEntry -> {
                PropertyValue propertyValue = stringToPropertyValueEntry.getValue();
                List valuesList = propertyValue.fuzz(fuzzer);
                if (valuesList != null && !valuesList.isEmpty()) {
                    propertyValue.setValue(valuesList.get(0));
                } else {
                    propertiesToDeleteList.add(stringToPropertyValueEntry.getKey());
                }
            });
        propertiesToDeleteList.forEach(nameToPropertyValueMap::remove);
    }
}
