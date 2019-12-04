package com.imperva.apiattacktool.processors;

import com.imperva.apiattacktool.fuzzing.Fuzzer;
import com.imperva.apiattacktool.fuzzing.modelgenerators.FuzzedModelsGenerator;
import com.imperva.apiattacktool.model.valued.EndpointValuedModel;
import com.imperva.apiattacktool.model.valued.factory.PropertyValueFactory;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FuzzEndpointModelProcessor implements EndpointModelProcessor {

    private FuzzedModelsGenerator fuzzedModelsGenerator;
    private PropertyValueFactory propertyValueFactory;

    public FuzzEndpointModelProcessor(FuzzedModelsGenerator fuzzedModelsGenerator) {
        this.fuzzedModelsGenerator = fuzzedModelsGenerator;
        this.propertyValueFactory = fuzzedModelsGenerator.getPropertyValueFactory();
    }

    @Override
    public List<EndpointValuedModel> process(List<EndpointValuedModel> endpointModelList, Fuzzer fuzzer) {
        if (endpointModelList == null) {
            return Collections.EMPTY_LIST;
        }

        return endpointModelList.stream()
            .flatMap(endpointValuedModel -> fuzzedModelsGenerator.fuzzModelValues(
                endpointValuedModel, fuzzer).stream())
            .collect(Collectors.toList());
    }
}
