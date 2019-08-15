package uk.gov.justice.services.generators.subscription.parser;

import static java.nio.file.Paths.get;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.fail;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

public class SubscriptionWrapperFileParserTest {

    private SubscriptionWrapperFileParser subscriptionWrapperFileParser = new SubscriptionWrapperFileParserFactory().create();

    @Test
    public void shouldParseEventSourceFromClasspathAndSubscriptionYamlFromPaths() {

        final Path path = getFromClasspath("");

        final List<Path> yamlPaths = singletonList(getFromClasspath("yaml/subscriptions-descriptor.yaml"));
        final Collection<SubscriptionWrapper> subscriptionWrapperCollection = subscriptionWrapperFileParser.parse(path, yamlPaths);

        assertThat(subscriptionWrapperCollection.size(), is(1));

        final SubscriptionWrapper subscriptionWrapper = subscriptionWrapperCollection.iterator().next();

        assertThat(subscriptionWrapper.getSubscriptionsDescriptor(), is(notNullValue()));
        assertThat(subscriptionWrapper.getEventSourceByName("event.processor.service").getName(), is("event.processor.service"));
    }

    @Test
    public void shouldThrowExceptionIfMoreThanOneEventSourcesPresent() {
        try {
            final Path path = getFromClasspath("");

            final List<Path> yamlPaths = asList(
                    getFromClasspath("yaml/event-sources.yaml"),
                    getFromClasspath("yaml/event-sources.yaml"),
                    getFromClasspath("yaml/event-processor/subscriptions-descriptor.yaml"));
            subscriptionWrapperFileParser.parse(path, yamlPaths);
            fail();
        } catch (final Exception expected) {
            assertThat(expected, is(instanceOf(RuntimeException.class)));
        }
    }

    @SuppressWarnings("ConstantConditions")
    private Path getFromClasspath(final String name) {
        final String fullPath = getClass().getClassLoader().getResource(name).getPath();

        return get(formatPathIfWindowsFileSystem(fullPath));
    }

    private String formatPathIfWindowsFileSystem(final String fullPath) {
        return fullPath.contains(":") ? fullPath.substring(fullPath.indexOf(":") + 1) : fullPath;
    }
}
