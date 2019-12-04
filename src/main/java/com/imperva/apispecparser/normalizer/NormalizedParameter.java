package com.imperva.apispecparser.normalizer;

public interface NormalizedParameter<T> {

    ApiDefinitions getApiDefinitions();

    int getTotalPropertiesCount();

    T getParameter();
}
