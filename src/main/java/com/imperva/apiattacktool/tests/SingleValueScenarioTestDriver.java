package com.imperva.apiattacktool.tests;

import com.imperva.apiattacktool.activators.EndpointModelToValueConverter;
import com.imperva.apiattacktool.activators.EndpointTestRequestDataConverter;
import com.imperva.apiattacktool.activators.TestHttpRequestGenerator;
import com.imperva.apiattacktool.fuzzing.Fuzzer;
import com.imperva.apiattacktool.fuzzing.modelgenerators.SingleValueFuzzedModelsGenerator;
import com.imperva.apiattacktool.fuzzing.parameters.PolicyEnforcer;
import com.imperva.apiattacktool.model.tests.EndpointTestRequestData;
import com.imperva.apiattacktool.model.tests.HttpRequestWrapper;
import com.imperva.apiattacktool.model.tests.PositiveTestHttpResponseValidator;
import com.imperva.apiattacktool.model.valued.EndpointValuedModel;
import com.imperva.apiattacktool.model.valued.factory.PropertyValueFactory;
import com.imperva.apiattacktool.processors.FuzzEndpointModelProcessor;
import com.imperva.apispecparser.model.EndpointModel;

import java.util.Collections;
import java.util.List;

public class SingleValueScenarioTestDriver extends AbstractTestDriver implements TestDriver {

    public SingleValueScenarioTestDriver(Fuzzer fuzzer,
                                         PolicyEnforcer policyEnforcer,
                                         PropertyValueFactory propertyValueFactory) {
        super(new EndpointModelToValueConverter(propertyValueFactory),
            policyEnforcer,
            null,
            new FuzzEndpointModelProcessor(new SingleValueFuzzedModelsGenerator(propertyValueFactory)),
            new EndpointTestRequestDataConverter(),
            new TestHttpRequestGenerator(PositiveTestHttpResponseValidator::new),
            fuzzer);
    }

    @Override
    public List<HttpRequestWrapper> getHttpRequestList(String resourceFileName) {
        List<EndpointModel> endpointModelList = parseSwagger(resourceFileName);
        if (endpointModelList.isEmpty()) {
            return Collections.emptyList();
        }

        List<EndpointValuedModel> endpointValuedModelList = getModelToValueConverter().endpointModelToEndpointValuedModel(endpointModelList);
        List<EndpointValuedModel> modelsWithPolicyEnforced = getPolicyEnforcer().enforcePolicyOn(endpointValuedModelList);
        List<EndpointValuedModel> fuzzedEndpointValuedModelList = getMainEndpointModelProcessor().process(modelsWithPolicyEnforced, getFuzzer());
        List<EndpointTestRequestData> endpointTestRequestDataList = getTestRequestDataConverter().processList(fuzzedEndpointValuedModelList);
        List<HttpRequestWrapper> httpRequestWrapperList = getHttpRequestGenerator().generateFrom(endpointTestRequestDataList);
        return httpRequestWrapperList;
    }
}
