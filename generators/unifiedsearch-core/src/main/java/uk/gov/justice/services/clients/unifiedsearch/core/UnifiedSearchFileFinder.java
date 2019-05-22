package uk.gov.justice.services.clients.unifiedsearch.core;

import static java.lang.String.format;

import java.net.URL;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UnifiedSearchFileFinder {

    private static final String TRANSFORMER_FILE_PATH = "transformer/";

    public URL getTransformerPaths(final String fileName) {
        final URL transformerPath = getClass().getClassLoader().getResource(TRANSFORMER_FILE_PATH.concat(fileName));
        if (transformerPath != null) {
            return transformerPath;
        }
        throw new UnifiedSearchException(format("Unable to find file on classpath: %s", fileName));
    }

}
