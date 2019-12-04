package com.imperva.apispecparser.parsers.swagger;

import com.imperva.apiattacktool.model.tests.ParameterLocation;
import com.imperva.apispecparser.exceptions.ParseException;
import com.imperva.apispecparser.model.EndpointModel;
import com.imperva.apispecparser.model.Property;
import com.imperva.apispecparser.normalizer.swagger.SwaggerDefinitionsNormalizer;
import com.imperva.apispecparser.normalizer.swagger.SwaggerNormalizedParameter;
import com.imperva.apispecparser.parsers.ApiSpecFileLocation;
import com.imperva.apispecparser.parsers.ApiSpecParser;
import com.imperva.apispecparser.parsers.swagger.property.SimpleSwaggerPropertyFactory;
import com.imperva.apispecparser.parsers.swagger.property.SwaggerAuthenticationToPropertyFactory;
import com.imperva.apispecparser.parsers.swagger.propertynode.SwaggerModelToPropertyNodeFactory;
import com.imperva.apispecparser.parsers.swagger.propertynode.SwaggerPropertyNodeConverter;
import com.imperva.apispecparser.utils.FileUtils;
import io.swagger.models.HttpMethod;
import io.swagger.models.Model;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Response;
import io.swagger.models.SecurityRequirement;
import io.swagger.models.Swagger;
import io.swagger.models.auth.SecuritySchemeDefinition;
import io.swagger.models.parameters.AbstractSerializableParameter;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.parser.SwaggerParser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Swagger2Parser implements ApiSpecParser {

    private static final Logger logger = LoggerFactory.getLogger(Swagger2Parser.class);

    @Override
    public List<EndpointModel> getEndpointModelList(String filePath, ApiSpecFileLocation apiSpecFileLocation) throws ParseException {
        List<EndpointModel> endpointModelList = new ArrayList<>();

        String fileContent = FileUtils.readFile(filePath, apiSpecFileLocation);

        if (fileContent == null) {
            return endpointModelList;
        }

        Swagger swagger = new SwaggerParser().parse(fileContent);

        for (Map.Entry<String, Path> pathEntry : swagger.getPaths().entrySet()) {
            for (Map.Entry<HttpMethod, Operation> httpMethodEntry : pathEntry.getValue().getOperationMap().entrySet()) {

                String fullPathWithParamBrackets = getNormalizedFullPath(swagger.getBasePath(), pathEntry.getKey());

                EndpointModel endpointModel = new EndpointModel(fullPathWithParamBrackets,
                    com.imperva.apiattacktool.model.tests.HttpMethod.valueOf(httpMethodEntry.getKey().toString()),
                    httpMethodEntry.getValue().getConsumes());

                for (Parameter parameter : httpMethodEntry.getValue().getParameters()) {
                    Map<String, Model> normalizedDefinitions;

                    if (parameter instanceof BodyParameter) {
                        BodyParameter bodyParameter = (BodyParameter) parameter;
                        SwaggerNormalizedParameter swaggerDefinitionNormalizerResult =
                            SwaggerDefinitionsNormalizer.normalizeParameterDefinitions(parameter, swagger.getDefinitions());
                        normalizedDefinitions = swaggerDefinitionNormalizerResult.getApiDefinitions().getDefinitions();
                        SwaggerPropertyNodeConverter swaggerPropertyNodeConverter = SwaggerModelToPropertyNodeFactory.get(bodyParameter.getSchema(),
                            bodyParameter.getRequired(), "", ParameterLocation.BODY, normalizedDefinitions);

                        if (swaggerPropertyNodeConverter != null) {
                            endpointModel.addNode(parameter.getName(), swaggerPropertyNodeConverter.getPropertyNode());
                        }
                    } else if (parameter instanceof AbstractSerializableParameter) {
                        AbstractSerializableParameter abstractSerializableParameter = (AbstractSerializableParameter) parameter;
                        Property property = SimpleSwaggerPropertyFactory.getPropertyFromAbstractSerializableParameter(abstractSerializableParameter,
                            parameter.getName(), swagger.getDefinitions());
                        endpointModel.addParameter(parameter.getName(), property);
                    }
                }

                addResponseCodesToEndpointModel(endpointModel, httpMethodEntry.getValue().getResponses());

                addSecurityConfiguration(endpointModel, swagger.getSecurityDefinitions(), swagger.getSecurity(), httpMethodEntry.getValue().getSecurity());

                endpointModelList.add(endpointModel);
            }
        }

        return endpointModelList;
    }

    private static void addSecurityConfiguration(EndpointModel endpointModel, Map<String, SecuritySchemeDefinition> securityDefinitions,
                                                 List<SecurityRequirement> apiLevelSecurity,
                                                 List<Map<String, List<String>>> endpointLevelSecurityConfiguration) {

        List<Map<String, List<String>>> securityRequirementsList = new ArrayList<>();

        if (endpointLevelSecurityConfiguration != null) {
            securityRequirementsList = endpointLevelSecurityConfiguration;

        } else if (apiLevelSecurity != null && apiLevelSecurity.size() > 0) {
            apiLevelSecurity.stream().map(SecurityRequirement::getRequirements).forEach(securityRequirementsList::add);
        }

        securityRequirementsList.forEach(securityRequirementMap -> {
            securityRequirementMap.forEach((key, value) -> {
                SecuritySchemeDefinition securitySchemeDefinition = securityDefinitions.get(key);
                Property property = SwaggerAuthenticationToPropertyFactory.get(securitySchemeDefinition);

                if (property != null) {
                    endpointModel.addParameter(property.getName(), property);
                }
            });
        });
    }

    private static void addResponseCodesToEndpointModel(EndpointModel endpointModel, Map<String, Response> responseStringToDefinitionMap) {
        responseStringToDefinitionMap.keySet().stream().forEach(httpResponseString -> {
            try {
                int httpReponseCode = Integer.parseInt(httpResponseString);
                endpointModel.addResponseCode(httpReponseCode);
            } catch (NumberFormatException badNumberException) {
                if (httpResponseString.equals("default")) {
                    endpointModel.addResponseCode(0);
                } else {
                    logger.error("Endpoint definition for: {} ({}), contained unparsable response code: {}. Skipping",
                        endpointModel.getFullPathWithParamBrackets(), endpointModel.getHttpMethod(), httpResponseString);
                }
            }
        });
    }

    public static String getNormalizedFullPath(String basePath, String path) {
        basePath = basePath == null ? "" : StringUtils.removeEnd(basePath.trim(), "/");
        path = path == null ? "" : StringUtils.removeEnd(StringUtils.removeStart(path.trim(), "/"), "/");
        return String.format("%s/%s", basePath, path);
    }
}
