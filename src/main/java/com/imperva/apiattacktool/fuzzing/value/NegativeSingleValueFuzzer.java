package com.imperva.apiattacktool.fuzzing.value;

import com.imperva.apiattacktool.model.tests.ParameterLocation;
import com.imperva.apiattacktool.model.valued.PropertyValueNode;
import com.imperva.apiattacktool.model.valued.factory.PropertyValueFactory;
import com.imperva.apispecparser.model.ArrayProperty;
import com.imperva.apispecparser.model.BooleanProperty;
import com.imperva.apispecparser.model.NumericProperty;
import com.imperva.apispecparser.model.Property;
import com.imperva.apispecparser.model.StringProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Fills trivial values
 */
public class NegativeSingleValueFuzzer extends CommonValueFuzzer implements ValueFuzzer {

    private static final Logger logger = LoggerFactory.getLogger(NegativeSingleValueFuzzer.class);
    private PropertyValueFactory propertyValueFactory;

    public NegativeSingleValueFuzzer(PropertyValueFactory propertyValueFactory) {
        this.propertyValueFactory = propertyValueFactory;
    }

    @Override
    public List<String> fuzz(Property property) {
        /*List<String> resultList = new ArrayList<>();
        resultList.add("negative untyped");
        return resultList;*/
        return null; // Disabling fuzzing, until we support file type
    }

    @Override
    public List<String> fuzz(BooleanProperty booleanProperty) {
        String[] invalidValues = new String[]
            {"troo", "phalse", "tru", "Fals", "falsee", "ttrue", "tTrue", "128", "-256", "4True", "rue", "alse"};
        int randomValueIndex = ThreadLocalRandom.current().nextInt(invalidValues.length);

        List<String> resultList = new ArrayList<>();
        resultList.add(invalidValues[randomValueIndex]);
        return resultList;
    }

    @Override
    public List<String> fuzz(StringProperty stringProperty) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        List<String> resultList = new LinkedList<>();
        /*if (stringProperty.getParameterLocation() == ParameterLocation.PATH) {
            String pathParamString = "#screwedPathParam";
            resultList.add(pathParamString);
            return resultList;
        }

        int generatedLength = 0;
        int minimumLength = stringProperty.getMinLength();
        if (minimumLength > 1) {
            generatedLength = minimumLength - 1;
        }

        int maximumLength = ThreadLocalRandom.current().nextInt(10) + 1;
        if (stringProperty.getMaxLength() != null) {
            maximumLength = stringProperty.getMaxLength();
        }
        if (minimumLength == 0) {
            generatedLength = maximumLength + 1;
        }

        int randomLengthFactor = random.nextInt(1, generatedLength);
        byte[] array = new byte[randomLengthFactor];
        random.nextBytes(array);
        String generatedString = new String(array, Charset.forName("UTF-16"));*/
        if (stringProperty.getParameterLocation() == ParameterLocation.PATH) { // Temporary hack, until we resolve how we check strings
            // This string is sent as is, and is not being encoded. So to fail Path param string, it is pretty straightforward (don't comply to the allowed character set in the URL).
            String generatedString = "{";
            resultList.add(generatedString);
            return resultList;
        } else {
            // We currently don't have a way to inject a negative value in a string. Next phase: generate negative values if it has length limits
            return null; // This would cause the negative fuzz to fail and be skipped.
        }
    }

    @Override
    public <T extends Number> List<Number> fuzz(NumericProperty<T> numericProperty) {
        List<Number> resultList = new ArrayList();
        Number randomNumber = getIncompatibleRandomNumberByType(numericProperty);
        resultList.add(randomNumber);
        return resultList;
    }


    @Override
    public List<List<PropertyValueNode>> fuzz(ArrayProperty arrayProperty) {
        return null; // Until next phase, disabling the negative fuzzing
        /*if (arrayProperty.getItems() == null) {
            return Collections.emptyList();
        }

        int generatedLength = 10;
        if (arrayProperty.getMaximumItems() != null) {
            generatedLength = arrayProperty.getMaximumItems() + 1;
        }

        if (arrayProperty.getMinimumItems() > 1) {
            generatedLength = arrayProperty.getMinimumItems() - 1;
        }

        PropertyNode arrayItemsPropertyNode = arrayProperty.getItems();
        PropertyValueNode propertyValueNode = new PropertyValueNode(arrayItemsPropertyNode, propertyValueFactory);
        List<PropertyValueNode> arrayValuesList = new LinkedList<>();
        for (int i = 0; i < generatedLength; i++) {
            PropertyValueNode fuzzedPropertyValueNode = traverseFuzzAndInjectPropertyValueNode(propertyValueNode, this);
            if (fuzzedPropertyValueNode == null) {
                logger.error("Could not clone PropertyValueNode: {}, while fuzzing. Skipping.", propertyValueNode);
                continue;
            }
            arrayValuesList.add(fuzzedPropertyValueNode);
        }

        List<List<PropertyValueNode>> resultList = new LinkedList<>();
        resultList.add(arrayValuesList);
        return resultList;*/
    }

    private Number getIncompatibleRandomNumberByType(NumericProperty numericProperty) {
        // TODO: add multipleOf handling
        switch (numericProperty.getType()) {
            case DECIMAL:
            case DOUBLE:
            case FLOAT:
                return ThreadLocalRandom.current().nextInt();
            case BASE_INTEGER:
            case INTEGER:
            default:
                /*float probabilityFactor = ThreadLocalRandom.current().nextFloat();
                int intMinimum = numericProperty.getMinimum() == null
                    ? Integer.MIN_VALUE
                    : numericProperty.getMinimum().intValue();
                int intMaximum = numericProperty.getMaximum() == null
                    ? Integer.MAX_VALUE
                    : numericProperty.getMaximum().intValue();
                if (numericProperty.getExclusiveMinimum()) {
                    intMinimum++; // So that random boundary catches it
                }
                if (!numericProperty.getExclusiveMaximum() && intMaximum != Integer.MAX_VALUE) {
                    intMaximum++; // So that we accidentally don't supply a valid value
                }
                if (probabilityFactor <= 0.33 && (intMinimum != Integer.MIN_VALUE || intMaximum != Integer.MAX_VALUE)) {
                    int randomInt;
                    if (intMinimum != Integer.MIN_VALUE) {
                        randomInt = ThreadLocalRandom.current().nextInt(Integer.MIN_VALUE, intMinimum);
                    } else {
                        randomInt = ThreadLocalRandom.current().nextInt(intMaximum, Integer.MAX_VALUE);
                    }
                    return randomInt;
                }
                if (probabilityFactor <= 0.66) {
                    long randomLong;
                    if (intMinimum != Integer.MIN_VALUE) {
                        randomLong = ThreadLocalRandom.current().nextLong(Long.MIN_VALUE, intMinimum);
                    } else {
                        randomLong = ThreadLocalRandom.current().nextLong(intMaximum, Long.MAX_VALUE);
                    }
                    return randomLong;
                }*/
            case LONG:
                float randomFloat = ThreadLocalRandom.current().nextFloat();
                return randomFloat;
        }
    }
}
