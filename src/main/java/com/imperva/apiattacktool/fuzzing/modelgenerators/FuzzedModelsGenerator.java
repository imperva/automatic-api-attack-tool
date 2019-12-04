package com.imperva.apiattacktool.fuzzing.modelgenerators;

import com.imperva.apiattacktool.fuzzing.Fuzzer;
import com.imperva.apiattacktool.model.valued.EndpointValuedModel;
import com.imperva.apiattacktool.model.valued.factory.PropertyValueFactory;

import java.util.List;

public interface FuzzedModelsGenerator {
    List<EndpointValuedModel> fuzzModelValues(EndpointValuedModel endpointValuedModel, Fuzzer fuzzer);

    PropertyValueFactory getPropertyValueFactory();
}
