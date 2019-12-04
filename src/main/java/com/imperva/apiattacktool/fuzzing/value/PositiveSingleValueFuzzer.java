package com.imperva.apiattacktool.fuzzing.value;

import com.imperva.apiattacktool.model.tests.ParameterLocation;
import com.imperva.apiattacktool.model.valued.PropertyValueNode;
import com.imperva.apiattacktool.model.valued.factory.PropertyValueFactory;
import com.imperva.apispecparser.model.ArrayProperty;
import com.imperva.apispecparser.model.BooleanProperty;
import com.imperva.apispecparser.model.EnumerableProperty;
import com.imperva.apispecparser.model.NumericProperty;
import com.imperva.apispecparser.model.Property;
import com.imperva.apispecparser.model.PropertyNode;
import com.imperva.apispecparser.model.StringProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Fills trivial values
 */
public class PositiveSingleValueFuzzer extends CommonValueFuzzer implements ValueFuzzer {

    private static final Logger logger = LoggerFactory.getLogger(PositiveSingleValueFuzzer.class);

    private PropertyValueFactory propertyValueFactory;

    public PositiveSingleValueFuzzer(PropertyValueFactory propertyValueFactory) {
        this.propertyValueFactory = propertyValueFactory;
    }

    @Override
    public List<String> fuzz(Property property) {
        List<String> resultList = new ArrayList<>();
        resultList.add("untyped");
        return resultList;
    }

    @Override
    public List<String> fuzz(BooleanProperty booleanProperty) {
        List<String> resultList = new ArrayList<>();
        Boolean enumValue = getRandomEnumValue(booleanProperty);
        if (enumValue != null) {
            resultList.add(String.valueOf(enumValue));
            return resultList;
        }

        resultList.add(String.valueOf(ThreadLocalRandom.current().nextBoolean()));
        return resultList;
    }

    @Override
    public List<String> fuzz(StringProperty stringProperty) {
        List<String> resultList = new ArrayList<>();
        ThreadLocalRandom random = ThreadLocalRandom.current();

        String enumValue = getRandomEnumValue(stringProperty);
        if (enumValue != null) {
            resultList.add(enumValue);
            return resultList;
        }

        if (stringProperty.getParameterLocation() == ParameterLocation.PATH) {
            String pathParamString = generateStringPathParamValue(stringProperty);
            resultList.add(pathParamString);
            return resultList;
        }

        int minimumLength = stringProperty.getMinLength();
        int maximumLength = random.nextInt(10) + minimumLength;
        if (stringProperty.getMaxLength() != null) {
            maximumLength = stringProperty.getMaxLength();
        }

        if (maximumLength <= minimumLength || maximumLength == 0) {
            maximumLength = minimumLength + 13;
        }

        int randomLengthFactor = random.nextInt(maximumLength - minimumLength + 1);
        byte[] array = new byte[minimumLength + randomLengthFactor];
        new Random().nextBytes(array);
        String generatedString = new String(array, Charset.forName("UTF-8"));

        resultList.add(generatedString);
        return resultList;
    }

    @Override
    public <T extends Number> List<Number> fuzz(NumericProperty<T> numericProperty) {
        List<Number> resultList = new ArrayList();
        T randomEnumValue = getRandomEnumValue(numericProperty);
        if (randomEnumValue != null) {
            resultList.add(randomEnumValue);
            return resultList;
        }

        Number randomNumber = getRandomNumberByType(numericProperty);
        resultList.add(randomNumber);
        return resultList;
    }

    @Override
    public List<List<PropertyValueNode>> fuzz(ArrayProperty arrayProperty) {
        if (arrayProperty.getItems() == null) {
            return Collections.emptyList();
        }

        // enums are handled within the specific property fuzzer, so we are OK
        int maximumItems = arrayProperty.getMaximumItems() != null ? arrayProperty.getMaximumItems() : 10;

        PropertyNode arrayItemsPropertyNode = arrayProperty.getItems();
        PropertyValueNode propertyValueNode = new PropertyValueNode(arrayItemsPropertyNode, propertyValueFactory);
        List<PropertyValueNode> arrayValuesList = new LinkedList<>();
        int randomNumberOfArrayElements = arrayProperty.getMinimumItems()
            + ThreadLocalRandom.current().nextInt(maximumItems - arrayProperty.getMinimumItems()) + 1;
        for (int i = 0; i < randomNumberOfArrayElements; i++) {
            PropertyValueNode fuzzedPropertyValueNode = traverseFuzzAndInjectPropertyValueNode(propertyValueNode, this);
            if (fuzzedPropertyValueNode == null) {
                logger.error("Could not clone PropertyValueNode: {}, while fuzzing. Skipping.", propertyValueNode);
                continue;
            }
            arrayValuesList.add(fuzzedPropertyValueNode);
        }

        List<List<PropertyValueNode>> resultList = new LinkedList<>();
        resultList.add(arrayValuesList);
        return resultList;
    }

    private String generateStringPathParamValue(StringProperty stringProperty) {
        ThreadLocalRandom currentRandomizer = ThreadLocalRandom.current();
        int maximumLength = stringProperty.getMaxLength() == null ? currentRandomizer.nextInt(1, 128) : stringProperty.getMaxLength();
        int minimumLength = stringProperty.getMinLength() > maximumLength ? 0 : stringProperty.getMinLength();

        StringBuilder stringBuilder = new StringBuilder();
        int maximumLengthOfGeneratedValue = minimumLength + currentRandomizer.nextInt(1, maximumLength - minimumLength + 1);
        while (stringBuilder.length() < maximumLengthOfGeneratedValue) {
            int maxLengthToGenerate = currentRandomizer.nextInt(1, maximumLengthOfGeneratedValue - stringBuilder.length() + 1);
            String generatedPart = generateStringPathParamValuePart(maxLengthToGenerate);
            stringBuilder.append(generatedPart);
        }
        return stringBuilder.toString();
    }
    
    private String generateStringPathParamValuePart(int maxLength) {
        // The backslash symbol is not supported by the Apache Http client, but may be sent as %5c
        // So even though the above RFC based regular expression expects it, we will not send it
        String validPathNotEncodedCharacters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-._~!$&'()*+,;=:@";
        String validPathEncodedCharacters = "0123456789abcdefABCDEF";
        ThreadLocalRandom currentRandomizer = ThreadLocalRandom.current();

        String currentCharacterSet;
        StringBuilder stringBuilder = new StringBuilder();
        if (maxLength > 2 && currentRandomizer.nextFloat() > 0.5) {
            currentCharacterSet = validPathEncodedCharacters;
            stringBuilder.append("%"); // encoded value prefix
            if (maxLength > 8) {
                maxLength = 8;
            }
            if (currentRandomizer.nextFloat() > 0.9) { // Make sure encoded backslash gets higher chance to appear
                stringBuilder.append("5C");
            }
        } else {
            currentCharacterSet = validPathNotEncodedCharacters;
        }

        int currentCharacterSetLength = currentCharacterSet.length();
        for (int i = stringBuilder.length(); i < maxLength; i++) {
            stringBuilder.append(currentCharacterSet.charAt(currentRandomizer.nextInt(currentCharacterSetLength)));
        }
        return stringBuilder.toString();
    }

    private Number getRandomNumberByType(NumericProperty numericProperty) {
        // TODO: add multipleOf handling
        switch (numericProperty.getType()) {
            case FLOAT:
                float floatMinimum = numericProperty.getMinimum() == null
                    ? Float.MIN_VALUE
                    : numericProperty.getMinimum().floatValue();
                float floatMaximum = numericProperty.getMaximum() == null
                    ? Float.MAX_VALUE
                    : numericProperty.getMaximum().floatValue();
                float randomFloat = ThreadLocalRandom.current().nextFloat() * (floatMaximum - floatMinimum) + floatMinimum;
                if (numericProperty.getExclusiveMinimum() && randomFloat == floatMinimum) {
                    randomFloat += 0.01;
                }
                return randomFloat;
            case LONG:
                long longMinimum = numericProperty.getMinimum() == null
                    ? Long.MIN_VALUE
                    : numericProperty.getMinimum().longValue();
                long longMaximum = numericProperty.getMaximum() == null
                    ? Long.MAX_VALUE
                    : numericProperty.getMaximum().longValue();
                long randomLong = ThreadLocalRandom.current().nextLong(longMinimum, longMaximum);
                if (numericProperty.getExclusiveMinimum() && randomLong == longMinimum) {
                    randomLong += 1;
                }
                return randomLong;
            case DECIMAL:
            case DOUBLE:
                double doubleMinimum = numericProperty.getMinimum() == null
                    ? Double.MIN_VALUE
                    : numericProperty.getMinimum().doubleValue();
                double doubleMaximum = numericProperty.getMaximum() == null
                    ? Double.MAX_VALUE
                    : numericProperty.getMaximum().doubleValue();
                double randomDouble = ThreadLocalRandom.current().nextDouble(doubleMinimum, doubleMaximum);
                if (numericProperty.getExclusiveMinimum() && randomDouble == doubleMinimum) {
                    randomDouble += 0.01;
                }
                return randomDouble;
            case BASE_INTEGER:
            case INTEGER:
            default:
                int intMinimum = numericProperty.getMinimum() == null
                    ? Integer.MIN_VALUE
                    : numericProperty.getMinimum().intValue();
                int intMaximum = numericProperty.getMaximum() == null
                    ? Integer.MAX_VALUE
                    : numericProperty.getMaximum().intValue();
                int randomInt = ThreadLocalRandom.current().nextInt(intMinimum, intMaximum);
                if (numericProperty.getExclusiveMinimum() && randomInt == intMinimum) {
                    randomInt += 1;
                }
                return randomInt;
        }
    }

    private <T> T getRandomEnumValue(EnumerableProperty<T> enumerableProperty) {
        if (enumerableProperty.getEnumList() != null && !enumerableProperty.getEnumList().isEmpty()) {
            int randomEnumItemIndex = ThreadLocalRandom.current().nextInt(enumerableProperty.getEnumList().size());
            return enumerableProperty.getEnumList().get(randomEnumItemIndex);
        }
        return null;
    }
}
