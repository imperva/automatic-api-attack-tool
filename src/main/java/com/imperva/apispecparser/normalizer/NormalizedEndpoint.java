package com.imperva.apispecparser.normalizer;

import java.util.Map;

public interface NormalizedEndpoint {

    String getName();

    Map<Integer, NormalizedParameter> getNormalizedParametersMap();

    void addNormalizedParameter(Integer parameterHashCode, NormalizedParameter normalizedParameter);

    NormalizedParameter getNormalizedParameter(Integer parameterHashCode);
}
