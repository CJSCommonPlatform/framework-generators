package uk.gov.justice.services.clients.unifiedsearch.core;


import uk.gov.justice.services.clients.unifiedsearch.core.domain.UnifiedSearchDescriptor;
import uk.gov.justice.services.yaml.YamlParser;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;


@ApplicationScoped
public class UnifiedSearchTransformerCache {

    @Inject
    private UnifiedSearchFileFinder unifiedSearchFileFinder;

    @Inject
    private FileContentsAsStringLoader fileContentsAsStringLoader;

    private Map<String, String> transformerCache = new ConcurrentHashMap<>();


    public String getTransformerConfigBy(final String eventName) {
        return transformerCache.get(eventName);
    }

    @PostConstruct
    public void populateCache() {
        final UnifiedSearchDescriptorYamlReader unifiedSearchDescriptorYamlReader = new UnifiedSearchDescriptorYamlReader(new YamlParser(), new YamlFileValidatorFactory().create());
        final UnifiedSearchDescriptor unifiedSearchDescriptor = unifiedSearchDescriptorYamlReader.getUnifiedSearchDescriptor();

        unifiedSearchDescriptor.getEvents().forEach(event -> transformerCache.put(event.getName(), jsonTransformerSpecReader(event.getTransformerConfig())));
    }

    private String jsonTransformerSpecReader(final String specFileName) {
        final URL transformerPath = unifiedSearchFileFinder.getTransformerPaths(specFileName);

        return fileContentsAsStringLoader.readFileContents(transformerPath);
    }

}
