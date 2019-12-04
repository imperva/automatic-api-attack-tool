package com.imperva.apispecparser.normalizer;

import java.util.Map;

public interface NormalizedPath {

    Map<Integer, NormalizedEndpoint> getNormalizedEndpointsMap();

    void addNormalizedEndpoint(Integer endpointHashCode, NormalizedEndpoint normalizedEndpoint);

    NormalizedEndpoint getNormalizedEndpoint(Integer endpointHashCode);
}
