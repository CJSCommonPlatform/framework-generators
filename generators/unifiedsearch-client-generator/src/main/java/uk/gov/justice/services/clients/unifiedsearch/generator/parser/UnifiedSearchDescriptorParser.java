package uk.gov.justice.services.clients.unifiedsearch.generator.parser;

import static java.util.stream.Collectors.toList;

import uk.gov.justice.maven.generator.io.files.parser.FileParser;
import uk.gov.justice.services.clients.unifiedsearch.core.domain.UnifiedSearchDescriptor;
import uk.gov.justice.services.generators.commons.helper.PathToUrlResolver;
import uk.gov.justice.services.yaml.YamlFileValidator;
import uk.gov.justice.services.yaml.YamlParser;

import java.net.URL;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Parse YAML URLs into {@link UnifiedSearchDescriptor}s
 */
public class UnifiedSearchDescriptorParser implements FileParser<UnifiedSearchDescriptor> {

    private static final String UNIFIED_SEARCH_SCHEMA_PATH = "/schema/unified-search-schema.json";

    private static final TypeReference<Map<String, UnifiedSearchDescriptor>> UNIFIED_SEARCH_DESCRIPTOR_TYPE_REF
            = new TypeReference<Map<String, UnifiedSearchDescriptor>>() {
    };

    private static final String UNIFIED_SEARCH_DESCRIPTOR = "unified_search_descriptor";

    private final YamlParser yamlParser;
    private final PathToUrlResolver pathToUrlResolver;
    private final YamlFileValidator yamlFileValidator;


    public UnifiedSearchDescriptorParser(final PathToUrlResolver pathToUrlResolver,
                                         final YamlParser yamlParser,
                                         final YamlFileValidator yamlFileValidator) {
        this.yamlParser = yamlParser;
        this.yamlFileValidator = yamlFileValidator;
        this.pathToUrlResolver = pathToUrlResolver;
    }

    @Override
    public Collection<UnifiedSearchDescriptor> parse(final Path baseDir, final Collection<Path> paths) {

        final List<URL> unifiedSearchYamlPaths = paths.stream()
                .map(path -> pathToUrlResolver.resolveToUrl(baseDir, path))
                .collect(toList());

        unifiedSearchYamlPaths.forEach(url -> yamlFileValidator.validate(UNIFIED_SEARCH_SCHEMA_PATH, url));

        return unifiedSearchYamlPaths.stream().map(this::parse).collect(toList());
    }

    private UnifiedSearchDescriptor parse(final URL url) {
        final Map<String, UnifiedSearchDescriptor> stringUnifiedSearchDescriptorMap = yamlParser.parseYamlFrom(url, UNIFIED_SEARCH_DESCRIPTOR_TYPE_REF);
        return stringUnifiedSearchDescriptorMap.get(UNIFIED_SEARCH_DESCRIPTOR);
    }

}
