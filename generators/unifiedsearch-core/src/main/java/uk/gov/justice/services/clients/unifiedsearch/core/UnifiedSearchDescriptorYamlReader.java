package uk.gov.justice.services.clients.unifiedsearch.core;

import uk.gov.justice.services.clients.unifiedsearch.core.domain.UnifiedSearchDescriptor;
import uk.gov.justice.services.yaml.YamlFileValidator;
import uk.gov.justice.services.yaml.YamlParser;

import java.net.URL;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;

public class UnifiedSearchDescriptorYamlReader {

    private static final String UNIFIED_SEARCH_DESCRIPTOR_KEY = "unified_search_descriptor";
    private static final String UNIFIED_SEARCH_DESCRIPTOR = "yaml/unified-search-descriptor.yaml";
    private static final String UNIFIED_SEARCH_SCHEMA_PATH = "/schema/unified-search-schema.json";

    private static final TypeReference<Map<String, UnifiedSearchDescriptor>> UNIFIED_SEARCH_DESCRIPTOR_TYPE_REF
            = new TypeReference<Map<String, UnifiedSearchDescriptor>>() {
    };

    private final YamlParser yamlParser;
    private final YamlFileValidator yamlFileValidator;


    public UnifiedSearchDescriptorYamlReader(final YamlParser yamlParser, YamlFileValidator yamlFileValidator) {
        this.yamlParser = yamlParser;
        this.yamlFileValidator = yamlFileValidator;
    }

    public UnifiedSearchDescriptor getUnifiedSearchDescriptor() {
        final URL url = getClass().getClassLoader().getResource(UNIFIED_SEARCH_DESCRIPTOR);

        yamlFileValidator.validate(UNIFIED_SEARCH_SCHEMA_PATH, url);

        final Map<String, UnifiedSearchDescriptor> stringUnifiedSearchDescriptorMap = yamlParser.parseYamlFrom(url, UNIFIED_SEARCH_DESCRIPTOR_TYPE_REF);
        return stringUnifiedSearchDescriptorMap.get(UNIFIED_SEARCH_DESCRIPTOR_KEY);
    }
}
