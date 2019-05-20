package uk.gov.justice.services.clients.unifiedsearch.core;


import uk.gov.justice.services.clients.unifiedsearch.core.domain.UnifiedSearchDescriptor;
import uk.gov.justice.services.clients.unifiedsearch.core.parser.UnifiedSearchDescriptorFileParserFactory;
import uk.gov.justice.services.clients.unifiedsearch.core.parser.UnifiedSearchDescriptorParser;

import java.net.URL;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;


@ApplicationScoped
public class UnifiedSearchTransformerCache {

    @Inject
    private UnifiedSearchFileFinder unifiedSearchFileFinder;

    @Inject
    private FileContentsAsStringLoader fileContentsAsStringLoader;

    @Inject
    private UnifiedSearchTransformerCacheUtils unifiedSearchTransformerCacheUtils;

    private Map<String, String> transformerCache = new ConcurrentHashMap<>();


    public String getTransformerConfigBy(final String eventName) {
        return transformerCache.get(eventName);
    }

    @PostConstruct
    public void populateCache() {
        final UnifiedSearchDescriptorParser unifiedSearchDescriptorFileParser = new UnifiedSearchDescriptorFileParserFactory().create();
        final List<Path> yamlPaths = unifiedSearchFileFinder.getUnifiedSearchDescriptor();
        final Path basePath = unifiedSearchFileFinder.getFromClasspath("");
        final Collection<UnifiedSearchDescriptor> unifiedSearchDescriptors = unifiedSearchDescriptorFileParser.parse(basePath, yamlPaths);

        unifiedSearchTransformerCacheUtils.verifyUnifiedSearchDescriptor(unifiedSearchDescriptors);

        final UnifiedSearchDescriptor unifiedSearchDescriptor = (UnifiedSearchDescriptor) CollectionUtils.get(unifiedSearchDescriptors, 0);

        unifiedSearchDescriptor.getEvents().forEach(event -> transformerCache.put(event.getName(), jsonTransformerSpecReader(event.getTransformerConfig())));
    }

    private String jsonTransformerSpecReader(final String specFileName) {
        final List<URL> transformerPaths = unifiedSearchFileFinder.getTransformerPaths(specFileName);

        unifiedSearchTransformerCacheUtils.verifyTransformerPaths(transformerPaths);

        return fileContentsAsStringLoader.readFileContents(transformerPaths.get(0));

    }
}
