package com.imperva.apispecparser.normalizer;

import java.util.Map;

public interface NormalizedApiSpec {

    Map<Integer, NormalizedPath> getPathHashCodeToNormalizedPathMap();

    void addNormalizedPath(Integer pathHashCode, NormalizedPath normalizedPath);

    NormalizedPath getNormalizedPath(Integer pathHashCode);
}
