package com.imperva.apispecparser.normalizer;

import java.util.HashMap;
import java.util.Map;

public abstract class NormalizedPathImpl implements NormalizedPath {

    private Map<Integer, NormalizedEndpoint> endpointHashCodeToNormalizedParameterMap = new HashMap<>();

    @Override
    public Map<Integer, NormalizedEndpoint> getNormalizedEndpointsMap() {
        return endpointHashCodeToNormalizedParameterMap;
    }

    @Override
    public void addNormalizedEndpoint(Integer endpointHashCode, NormalizedEndpoint normalizedEndpoint) {
        endpointHashCodeToNormalizedParameterMap.put(endpointHashCode, normalizedEndpoint);
    }

    @Override
    public NormalizedEndpoint getNormalizedEndpoint(Integer endpointHashCode) {
        return endpointHashCodeToNormalizedParameterMap == null ? null : endpointHashCodeToNormalizedParameterMap.get(endpointHashCode);
    }
}
