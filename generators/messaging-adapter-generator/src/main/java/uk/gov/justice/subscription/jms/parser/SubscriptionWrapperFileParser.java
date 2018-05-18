package uk.gov.justice.subscription.jms.parser;

import uk.gov.justice.maven.generator.io.files.parser.FileParser;
import uk.gov.justice.subscription.domain.eventsource.EventSourceDefinition;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public class SubscriptionWrapperFileParser implements FileParser<SubscriptionWrapper> {

    private final EventSourcesFileParser eventSourcesFileParser;
    private final SubscriptionDescriptorFileParser subscriptionDescriptorFileParser;

    public SubscriptionWrapperFileParser(final EventSourcesFileParser eventSourcesFileParser,
                                         final SubscriptionDescriptorFileParser subscriptionDescriptorFileParser) {
        this.eventSourcesFileParser = eventSourcesFileParser;
        this.subscriptionDescriptorFileParser = subscriptionDescriptorFileParser;
    }

    @Override
    public Collection<SubscriptionWrapper> parse(final Path baseDir, final Collection<Path> paths) {
        final List<EventSourceDefinition> eventSourceDefinitions = eventSourcesFileParser.getEventSourceDefinitions(baseDir, paths);
        return subscriptionDescriptorFileParser.getSubscriptionWrappers(baseDir, paths, eventSourceDefinitions);
    }
}
