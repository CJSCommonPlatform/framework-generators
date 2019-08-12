package uk.gov.justice.services.generators.subscription.parser;

import static java.util.Collections.list;

import uk.gov.justice.services.generators.commons.helper.FileParserException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class EventSourceYamlClasspathFinder {

    public List<URL> getEventSourcesPaths() {
        try {
            return list(this.getClass().getClassLoader().getResources("yaml/event-sources.yaml"));
        } catch (final IOException e) {
            throw new FileParserException("Failed when requesting event-sources.yaml from classpath", e);
        }
    }
}
