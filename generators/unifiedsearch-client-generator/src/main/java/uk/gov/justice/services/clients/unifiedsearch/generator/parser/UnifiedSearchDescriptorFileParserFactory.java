package uk.gov.justice.services.clients.unifiedsearch.generator.parser;

import uk.gov.justice.maven.generator.io.files.parser.FileParserFactory;
import uk.gov.justice.services.clients.unifiedsearch.core.YamlFileValidatorFactory;
import uk.gov.justice.services.clients.unifiedsearch.core.domain.UnifiedSearchDescriptor;
import uk.gov.justice.services.generators.commons.helper.PathToUrlResolver;
import uk.gov.justice.services.yaml.YamlParser;


public class UnifiedSearchDescriptorFileParserFactory implements FileParserFactory<UnifiedSearchDescriptor> {

    @Override
    public UnifiedSearchDescriptorParser create() {
        final YamlParser yamlParser = new YamlParser();
        final PathToUrlResolver pathToUrlResolver = new PathToUrlResolver();

        return new UnifiedSearchDescriptorParser(pathToUrlResolver, yamlParser, new YamlFileValidatorFactory().create());
    }
}
