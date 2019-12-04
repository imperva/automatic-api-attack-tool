package com.imperva.apiattacktool.processors;

import com.imperva.apiattacktool.fuzzing.Fuzzer;
import com.imperva.apiattacktool.model.valued.EndpointValuedModel;

import java.util.List;

public interface EndpointModelProcessor {
    List<EndpointValuedModel> process(List<EndpointValuedModel> endpointModelList, Fuzzer fuzzer);
}
