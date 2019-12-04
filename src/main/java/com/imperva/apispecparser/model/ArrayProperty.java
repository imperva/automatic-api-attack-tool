package com.imperva.apispecparser.model;

import java.util.HashMap;
import java.util.Map;

public class ArrayProperty extends Property {
    private Boolean areItemsUnique;
    private PropertyNode items;
    private int minimumItems;
    private Integer maximumItems;
    private CollectionFormat collectionFormat;

    public ArrayProperty(Property property, Boolean areItemsUnique, PropertyNode items, Integer minItems, Integer maxItems, String collectionFormat) {
        super(property);
        this.areItemsUnique = areItemsUnique;
        this.items = items;
        this.minimumItems = minItems == null ? 0 : minItems;
        this.maximumItems = maxItems;
        this.collectionFormat = CollectionFormat.getBySpecCollectionFormat(collectionFormat); // Sets CSV as default
    }

    public Boolean getAreItemsUnique() {
        return areItemsUnique;
    }

    public PropertyNode getItems() {
        return items;
    }

    public int getMinimumItems() {
        return minimumItems;
    }

    public Integer getMaximumItems() {
        return maximumItems;
    }

    public CollectionFormat getCollectionFormat() {
        return collectionFormat;
    }

    @Override
    public String toString() {
        return "ArrayProperty{"
            + "areItemsUnique=" + areItemsUnique
            + ", items=" + items
            + ", minimumItems=" + minimumItems
            + ", maximumItems=" + maximumItems
            + ", Property=" + super.toString()
            + '}';
    }

    public enum CollectionFormat {
        COMMA_SEPARATED_VALUES("csv", ","),
        SPACE_SEPARATED_VALUES("ssv", "\\s"),
        TAB_SEPARATED_VALUES("tsv", "\\t"),
        PIPE_SEPARATED_VALUES("pipes", "\\|"),
        AMPERSAND_CONCATENATED_VALUES("multi", "&");

        private static final Map<String, CollectionFormat> enumValuesMap = initializeMapping();
        private String formatString;
        private String outputString;

        CollectionFormat(String formatString, String outputString) {
            this.formatString = formatString;
            this.outputString = outputString;
        }

        private static Map<String, CollectionFormat> initializeMapping() {
            Map<String, CollectionFormat> enumValuesMap = new HashMap<>();
            for (CollectionFormat collectionFormat : CollectionFormat.values()) {
                enumValuesMap.put(collectionFormat.formatString, collectionFormat);
            }
            return enumValuesMap;
        }

        public static CollectionFormat getBySpecCollectionFormat(String specFormat) {
            CollectionFormat collectionFormat = enumValuesMap.get(specFormat);
            if (collectionFormat == null) {
                collectionFormat = COMMA_SEPARATED_VALUES;
            }
            return collectionFormat;
        }

        public String getFormatString() {
            return formatString;
        }

        public String getOutputString() {
            return outputString;
        }
    }
}
