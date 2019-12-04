package com.imperva.apispecparser.normalizer;

import java.util.HashMap;
import java.util.Map;

public abstract class NormalizedEndpointImpl implements NormalizedEndpoint {

    private final String name;
    private Map<Integer, NormalizedParameter> parameterHashCodeToNormalizedParameterMap = new HashMap<>();

    public NormalizedEndpointImpl(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Map<Integer, NormalizedParameter> getNormalizedParametersMap() {
        return parameterHashCodeToNormalizedParameterMap;
    }

    @Override
    public void addNormalizedParameter(Integer parameterHashCode, NormalizedParameter normalizedParameter) {
        parameterHashCodeToNormalizedParameterMap.put(parameterHashCode, normalizedParameter);
    }

    @Override
    public NormalizedParameter getNormalizedParameter(Integer parameterHashCode) {
        if (parameterHashCodeToNormalizedParameterMap == null) {
            return null;
        }

        return parameterHashCodeToNormalizedParameterMap.get(parameterHashCode);
    }
}
