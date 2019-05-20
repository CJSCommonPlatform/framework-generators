package uk.gov.justice.services.clients.unifiedsearch.core;

import static java.lang.String.format;
import static java.nio.file.Paths.get;
import static java.util.Arrays.asList;
import static java.util.Collections.list;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UnifiedSearchFileFinder {

    private static final String TRANSFORMER_FILE_PATH = "transformer/";
    private static final String UNIFIED_SEARCH_DESCRIPTOR = "yaml/unified-search-descriptor.yaml";

    public List<Path> getUnifiedSearchDescriptor() {
        final URL url = findOnClasspath(UNIFIED_SEARCH_DESCRIPTOR)
                .stream()
                .findFirst()
                .orElseThrow(() -> new UnifiedSearchException(String.format("Unable to find file %s on classpath", UNIFIED_SEARCH_DESCRIPTOR)));

        return asList(get(url.getPath()));
    }

    public List<URL> getTransformerPaths(final String fileName) {
        final List<URL> transformerPaths = findOnClasspath(TRANSFORMER_FILE_PATH.concat(fileName));
        if (!transformerPaths.isEmpty()) {
            return transformerPaths;
        }
        throw new UnifiedSearchException(format("Unable to find file on classpath: %s", fileName));
    }


    private List<URL> findOnClasspath(final String name) {
        try {
            return list(getClass().getClassLoader().getResources(name));
        } catch (final IOException ioe) {
            throw new UnifiedSearchException(format("Unable to find file on classpath: %s", name), ioe);
        }
    }

    @SuppressWarnings("ConstantConditions")
    public Path getFromClasspath(final String name) {
        final String fullPath = getClass().getClassLoader().getResource(name).getPath();

        return get(formatPathIfWindowsFileSystem(fullPath));
    }

    private String formatPathIfWindowsFileSystem(final String fullPath) {
        return fullPath.contains(":") ? fullPath.substring(fullPath.indexOf(':') + 1) : fullPath;
    }

}
