package uk.gov.justice.services.generators.subscription.parser;

import static java.util.stream.Collectors.toList;

import uk.gov.justice.services.generators.commons.helper.FileParserException;
import uk.gov.justice.services.generators.commons.helper.PathToUrlResolver;
import uk.gov.justice.subscription.EventSourcesParser;
import uk.gov.justice.subscription.domain.eventsource.EventSourceDefinition;

import java.net.URL;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class EventSourcesFileParser {

    private final EventSourcesParser eventSourcesParser;
    private final PathToUrlResolver pathToUrlResolver;
    private final EventSourceYamlClasspathFinder eventSourceYamlClasspathFinder;

    public EventSourcesFileParser(final EventSourcesParser eventSourcesParser,
                                  final PathToUrlResolver pathToUrlResolver,
                                  final EventSourceYamlClasspathFinder eventSourceYamlClasspathFinder) {
        this.eventSourcesParser = eventSourcesParser;
        this.pathToUrlResolver = pathToUrlResolver;
        this.eventSourceYamlClasspathFinder = eventSourceYamlClasspathFinder;
    }

    public List<EventSourceDefinition> getEventSourceDefinitions(final Path baseDir, final Collection<Path> paths) {

        final List<URL> eventSourcesPaths = paths.stream()
                .filter(isEventSource)
                .map(path -> pathToUrlResolver.resolveToUrl(baseDir, path))
                .collect(toList());

        eventSourcesPaths.addAll(eventSourceYamlClasspathFinder.getEventSourcesPaths());

        if (eventSourcesPaths.isEmpty()) {
            throw new FileParserException("No event-sources.yaml files found!");
        }

        return eventSourcesParser.eventSourcesFrom(eventSourcesPaths).collect(toList());
    }

    private Predicate<Path> isEventSource = path -> path.endsWith("event-sources.yaml");
}
