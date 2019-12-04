package com.imperva.apiattacktool.activators;

import com.imperva.apiattacktool.model.tests.EndpointTestRequestData;
import com.imperva.apiattacktool.model.tests.HttpMethod;
import com.imperva.apiattacktool.model.tests.HttpRequestWrapper;
import com.imperva.apiattacktool.model.tests.HttpResponseValidator;
import com.imperva.apiattacktool.model.valued.PropertyValue;
import org.apache.http.HttpRequest;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.DefaultHttpRequestFactory;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TestHttpRequestGenerator implements HttpRequestGenerator {

    private static final Logger logger = LoggerFactory.getLogger(TestHttpRequestGenerator.class);
    private Function<Collection<Integer>, HttpResponseValidator> httpResponseValidatorGenerator;

    public TestHttpRequestGenerator(Function<Collection<Integer>, HttpResponseValidator> httpResponseValidatorGenerator) {
        this.httpResponseValidatorGenerator = httpResponseValidatorGenerator;
    }

    @Override
    public List<HttpRequestWrapper> generateFrom(List<EndpointTestRequestData> endpointTestRequestDataList) {
        if (endpointTestRequestDataList == null) {
            return Collections.emptyList();
        }
        List<HttpRequestWrapper> httpRequestList =
            endpointTestRequestDataList.stream()
                .map(endpointTestRequestData -> {
                    try {
                        return generateFrom(endpointTestRequestData);
                    } catch (MethodNotSupportedException methodNotSupportedException) {
                        logger.error("Could not create create HttpRequest for this endpoint: {}", endpointTestRequestData, methodNotSupportedException);
                        return null; // swallow and continue
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return httpRequestList;
    }

    private HttpRequestWrapper generateFrom(EndpointTestRequestData endpointTestRequestData) throws MethodNotSupportedException {
        HttpRequest httpRequest;
        HttpMethod httpMethod = endpointTestRequestData.getHttpMethod();
        try {
            String url = endpointTestRequestData.getEndpointUrl();
            if (endpointTestRequestData.hasQueryParameters()) {
                url = attachQueryParametersToUrl(endpointTestRequestData.getEndpointUrl(), endpointTestRequestData.getQueryParameters());
            }

            if (endpointTestRequestData.getHttpMethod() != HttpMethod.PATCH) {
                httpRequest = DefaultHttpRequestFactory.INSTANCE.newHttpRequest(httpMethod.toString(), url);
            } else {
                httpRequest = new BasicHttpEntityEnclosingRequest(httpMethod.toString(), url);
            }
            if (endpointTestRequestData.hasHeaderParameters()) {
                updateRequestWithRequestHeaders(httpRequest, endpointTestRequestData.getHeaderParameters());
            }
        } catch (URISyntaxException uriSyntaxException) {
            logger.error("Couldn't generate http request for {}", endpointTestRequestData, uriSyntaxException);
            return null;
        }

        if (httpRequest instanceof BasicHttpEntityEnclosingRequest) {
            BasicHttpEntityEnclosingRequest basicHttpEntityEnclosingRequest = (BasicHttpEntityEnclosingRequest) httpRequest;
            if (endpointTestRequestData.hasBodyParameters()) {
                String bodyString = endpointTestRequestData.getBodyParameter();
                logger.debug("Endpoint: {} [{}], Body: {}", endpointTestRequestData.getEndpointUrl(), httpMethod, bodyString);
                if (bodyString != null) {
                    try {
                        StringEntity entity = new StringEntity(endpointTestRequestData.getBodyParameter(), "UTF-8");
                        basicHttpEntityEnclosingRequest.setEntity(entity);
                    } catch (Exception anyException) {
                        logger.error("Encountered an error while setting body string for endpoint url: {}, method: {}",
                            endpointTestRequestData.getEndpointUrl(), endpointTestRequestData.getHttpMethod(), anyException);
                    }
                }
            } else if (endpointTestRequestData.hasFormDataParameters()) {
                MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
                endpointTestRequestData.getFormDataParameters().forEach(propertyValue ->
                    multipartEntityBuilder.addPart(propertyValue.getName(), formDataToContentBody(propertyValue,
                        endpointTestRequestData.getConsumesMimeTypes())));
                basicHttpEntityEnclosingRequest.setEntity(multipartEntityBuilder.build());
            }
        }

        HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(httpRequest, endpointTestRequestData.getTestComment(),
            httpResponseValidatorGenerator.apply(endpointTestRequestData.getHttpResponseCodesCollection()));
        return httpRequestWrapper;
    }

    private ContentBody formDataToContentBody(PropertyValue formDataPropertyValue, List<String> consumesMimeTypes) {
        String mimeTypeString = (consumesMimeTypes == null || consumesMimeTypes.isEmpty())
            ? ContentType.APPLICATION_FORM_URLENCODED.getMimeType()
            : consumesMimeTypes.get(0);

        switch (formDataPropertyValue.getType()) {
            case FILE:
            case BYTE_ARRAY:
                // not implemented right now, but should use ByteArrayBody
            case ARRAY:
            case FLOAT:
            case LONG:
            case DECIMAL:
            case DOUBLE:
            case BASE_INTEGER:
            case INTEGER:
            case UUID:
            case PASSWORD:
            case EMAIL:
            case DATETIME:
            case DATE:
            case BINARY:
            case STRING:
            case BOOLEAN:
                return new StringBody(String.valueOf(formDataPropertyValue.getValue()), ContentType.getByMimeType(mimeTypeString));
            case UNTYPED:
            default:
                return new StringBody(String.valueOf(formDataPropertyValue.getValue()), ContentType.TEXT_PLAIN);
        }
    }

    private void updateRequestWithRequestHeaders(HttpRequest httpRequest, List<PropertyValue> headerParameters) {
        headerParameters.forEach(propertyValue -> httpRequest.addHeader(propertyValue.getName(), String.valueOf(propertyValue.getValue())));
    }

    private String attachQueryParametersToUrl(String url, List<PropertyValue> queryParameters) throws URISyntaxException {
        URIBuilder builder = new URIBuilder(url);
        queryParameters.forEach(queryParam -> builder.addParameter(queryParam.getName(), String.valueOf(queryParam.getValue())));
        return builder.build().toString();
    }

    // Add cookie handling
}
