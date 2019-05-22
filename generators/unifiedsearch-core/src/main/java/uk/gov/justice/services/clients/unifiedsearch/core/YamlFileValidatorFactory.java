package uk.gov.justice.services.clients.unifiedsearch.core;

import uk.gov.justice.services.common.converter.jackson.ObjectMapperProducer;
import uk.gov.justice.services.yaml.YamlFileValidator;
import uk.gov.justice.services.yaml.YamlParser;
import uk.gov.justice.services.yaml.YamlSchemaLoader;
import uk.gov.justice.services.yaml.YamlToJsonObjectConverter;

import com.fasterxml.jackson.databind.ObjectMapper;

public class YamlFileValidatorFactory {

    public YamlFileValidator create(){
        final YamlParser yamlParser = new YamlParser();

        final YamlSchemaLoader yamlSchemaLoader = new YamlSchemaLoader();
        final ObjectMapper objectMapper = new ObjectMapperProducer().objectMapper();
        final YamlToJsonObjectConverter yamlToJsonObjectConverter = new YamlToJsonObjectConverter(yamlParser, objectMapper);

        return new YamlFileValidator(yamlToJsonObjectConverter, yamlSchemaLoader);
    }
}
