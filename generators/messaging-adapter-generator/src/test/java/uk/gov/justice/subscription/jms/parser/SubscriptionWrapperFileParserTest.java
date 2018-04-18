package uk.gov.justice.subscription.jms.parser;

import static java.nio.file.Paths.get;
import static java.util.Arrays.asList;
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
    public void shouldParseEventSourceAndSubscriptionYaml() throws Exception {

        final Path path = getFromClasspath("");

        final List<Path> yamlPaths = asList(
                getFromClasspath("yaml/event-sources.yaml"),
                getFromClasspath("yaml/command-controller.yaml"));
        final Collection<SubscriptionWrapper> subscriptionWrapperCollection = subscriptionWrapperFileParser.parse(path, yamlPaths);

        assertThat(subscriptionWrapperCollection.size(), is(1));

        final SubscriptionWrapper subscriptionWrapper = subscriptionWrapperCollection.iterator().next();

        assertThat(subscriptionWrapper.getSubscriptionDescriptor(), is(notNullValue()));
        assertThat(subscriptionWrapper.getEventSourceByName("command.controller.service"), is(notNullValue()));
    }

    @Test
    public void shouldThrowExceptionIfMoreThanOneEventSourcesPresent() throws Exception {
        try {
            final Path path = getFromClasspath("");

            final List<Path> yamlPaths = asList(
                    getFromClasspath("yaml/event-sources.yaml"),
                    getFromClasspath("yaml/event-sources.yaml"),
                    getFromClasspath("yaml/command-controller.yaml"));
            subscriptionWrapperFileParser.parse(path, yamlPaths);
            fail();
        } catch (final Exception expected) {
            assertThat(expected, is(instanceOf(RuntimeException.class)));
        }
    }

    @SuppressWarnings("ConstantConditions")
    private Path getFromClasspath(final String name) {
        return get(getClass().getClassLoader().getResource(name).getPath());
    }
}
