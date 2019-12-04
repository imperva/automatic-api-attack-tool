package com.imperva.apispecparser.normalizer;

import java.util.HashMap;
import java.util.Map;

public abstract class NormalizedApiSpecImpl implements NormalizedApiSpec {

    private Map<Integer, NormalizedPath> pathHashCodeToNormalizedPathMap = new HashMap<>();

    @Override
    public Map<Integer, NormalizedPath> getPathHashCodeToNormalizedPathMap() {
        return pathHashCodeToNormalizedPathMap;
    }

    @Override
    public void addNormalizedPath(Integer pathHashCode, NormalizedPath normalizedPath) {
        pathHashCodeToNormalizedPathMap.put(pathHashCode, normalizedPath);
    }

    @Override
    public NormalizedPath getNormalizedPath(Integer pathHashCode) {
        return pathHashCodeToNormalizedPathMap.get(pathHashCode);
    }
}
