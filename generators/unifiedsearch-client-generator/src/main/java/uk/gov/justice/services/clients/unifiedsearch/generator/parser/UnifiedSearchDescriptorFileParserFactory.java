package uk.gov.justice.services.clients.unifiedsearch.generator.parser;

import uk.gov.justice.maven.generator.io.files.parser.FileParserFactory;
import uk.gov.justice.services.clients.unifiedsearch.generator.domain.UnifiedSearchDescriptor;
import uk.gov.justice.services.common.converter.jackson.ObjectMapperProducer;
import uk.gov.justice.services.generators.commons.helper.PathToUrlResolver;
import uk.gov.justice.services.yaml.YamlFileValidator;
import uk.gov.justice.services.yaml.YamlParser;
import uk.gov.justice.services.yaml.YamlSchemaLoader;
import uk.gov.justice.services.yaml.YamlToJsonObjectConverter;

import com.fasterxml.jackson.databind.ObjectMapper;


public class UnifiedSearchDescriptorFileParserFactory implements FileParserFactory<UnifiedSearchDescriptor> {

    @Override
    public UnifiedSearchDescriptorParser create() {
        final YamlParser yamlParser = new YamlParser();
        final YamlSchemaLoader yamlSchemaLoader = new YamlSchemaLoader();
        final ObjectMapper objectMapper = new ObjectMapperProducer().objectMapper();
        final YamlToJsonObjectConverter yamlToJsonObjectConverter = new YamlToJsonObjectConverter(yamlParser, objectMapper);

        final PathToUrlResolver pathToUrlResolver = new PathToUrlResolver();
        return new UnifiedSearchDescriptorParser(pathToUrlResolver, yamlParser, new YamlFileValidator(yamlToJsonObjectConverter, yamlSchemaLoader));
    }
}
