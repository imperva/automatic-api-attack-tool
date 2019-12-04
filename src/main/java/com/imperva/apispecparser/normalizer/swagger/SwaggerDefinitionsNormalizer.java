package com.imperva.apispecparser.normalizer.swagger;

import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.RefModel;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SwaggerDefinitionsNormalizer {

    private static Logger logger = LoggerFactory.getLogger(SwaggerDefinitionsNormalizer.class);

    public static SwaggerNormalizedParameter normalizeParameterDefinitions(Parameter parameter,
                                                                           Map<String, Model> swaggerFullDefinitions) {
        SwaggerNormalizedParameter swaggerDefinitionNormalizerResult;

        if (parameter instanceof BodyParameter) {
            BodyParameter bodyParameter = (BodyParameter) parameter;
            Model model = bodyParameter.getSchema();
            swaggerDefinitionNormalizerResult = SwaggerDefinitionsNormalizer.normalizeModelDefinitions(null, model, null, "",
                swaggerFullDefinitions, new HashSet<>(), new SwaggerNormalizedParameter<>(parameter));
        } else {
            swaggerDefinitionNormalizerResult = new SwaggerNormalizedParameter<>(parameter, swaggerFullDefinitions, 1);
        }

        return swaggerDefinitionNormalizerResult;
    }

    private static SwaggerNormalizedParameter normalizeModelDefinitions(String currentModelName, Model currentModel, Model parentModel, String ref,
                                                                        Map<String, Model> swaggerFullDefinitions, Set<String> scannedModels,
                                                                        SwaggerNormalizedParameter swaggerDefinitionNormalizerResult) {

        Model filteredModel = null;

        if (swaggerDefinitionNormalizerResult.containsDefinition(currentModelName)) {
            filteredModel = swaggerDefinitionNormalizerResult.getDefinition(currentModelName);
        } else if (currentModel != null) {
            filteredModel = (Model) currentModel.clone();

            if (currentModelName != null) {
                swaggerDefinitionNormalizerResult.addDefinition(currentModelName, filteredModel);
            }
        }

        if (filteredModel instanceof RefModel) {
            RefModel refModel = (RefModel) currentModel;
            String definitionName = refModel.getSimpleRef();
            Model definitionModel = swaggerFullDefinitions.get(definitionName);
            Model newParentModel = parentModel == null ? null : currentModel; //parentModel will be null only on first iteration

            //Remove Ref model (definition) that point to itself
            if (definitionModel == currentModel) {
                swaggerDefinitionNormalizerResult.removeDefinition(currentModelName);
                return swaggerDefinitionNormalizerResult;
            }

            normalizeModelDefinitions(definitionName, definitionModel, newParentModel, ref + "/" + definitionName, swaggerFullDefinitions,
                scannedModels, swaggerDefinitionNormalizerResult);
        } else if (filteredModel instanceof ModelImpl) {
            ModelImpl currentModelImpl = (ModelImpl) currentModel;
            ModelImpl filteredModelImpl = (ModelImpl) filteredModel;

            //Workaround to bug in swagger model clone which ignore some fields
            filteredModelImpl._enum(currentModelImpl.getEnum());
            filteredModelImpl.format(currentModelImpl.getFormat());
            filteredModelImpl.allowEmptyValue(currentModelImpl.getAllowEmptyValue());
            filteredModelImpl.uniqueItems(currentModelImpl.getUniqueItems());


            Map<String, Property> propertyMap = currentModelImpl.getProperties();

            if (propertyMap != null) {
                for (String propertyName : currentModelImpl.getProperties().keySet()) {
                    Property property = currentModelImpl.getProperties().get(propertyName);

                    Set<String> propertyScannedModels;

                    //If parent model is null this means we're dealing with a new property (not part of the recursion)
                    //In this case, the 'scannedModels' should be initiated (every single property should calculate loop in its own tree)
                    if (parentModel == null) {
                        propertyScannedModels = new HashSet<>();
                    } else {
                        propertyScannedModels = scannedModels;
                    }

                    propertyScannedModels.add(currentModelName);

                    filterProperty(currentModelName, filteredModel, property, propertyName, ref, swaggerFullDefinitions, propertyScannedModels,
                        swaggerDefinitionNormalizerResult);
                }
            }
        }

        return swaggerDefinitionNormalizerResult;
    }

    private static void filterProperty(String parentModelName, Model parentFilteredModel, Property property, String propertyName, String ref,
                                       Map<String, Model> swaggerFullDefinitions, Set<String> scannedModels,
                                       SwaggerNormalizedParameter swaggerDefinitionNormalizerResult) {

        if (property instanceof RefProperty) {
            RefProperty refProperty = (RefProperty) property;
            String refPropertyName = refProperty.getSimpleRef();
            Model definitionModel = swaggerFullDefinitions.get(refPropertyName);

            String propertyRef = ref + " -> " + refPropertyName + " (" + propertyName + ")";

            if (scannedModels.contains(refPropertyName)) {
                if (parentFilteredModel.getProperties().containsKey(propertyName)) {
                    logger.info("Removing circular property [{}] from Model [{}], Ref: {}", propertyName, parentModelName, propertyRef);
                    parentFilteredModel.getProperties().remove(propertyName);
                }
            } else {
                normalizeModelDefinitions(refPropertyName, definitionModel, parentFilteredModel, propertyRef, swaggerFullDefinitions, scannedModels,
                    swaggerDefinitionNormalizerResult);
            }

        } else if (property instanceof ArrayProperty) {
            ArrayProperty arrayProperty = (ArrayProperty) property;
            filterProperty(parentModelName, parentFilteredModel, arrayProperty.getItems(), propertyName, ref, swaggerFullDefinitions, scannedModels,
                swaggerDefinitionNormalizerResult);
        } else {
            swaggerDefinitionNormalizerResult.incrementTotalPropertiesCount();
        }
    }
}
