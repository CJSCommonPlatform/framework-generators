package uk.gov.justice.services.generators.subscription.parser;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.net.URL;
import java.util.List;

import org.junit.Test;

public class EventSourceYamlClasspathFinderTest {

    @Test
    public void shouldFindEventSourcesYamlFromClasspath() {

        final List<URL> eventSourcesPaths = new EventSourceYamlClasspathFinder().getEventSourcesPaths();

        assertThat(eventSourcesPaths.size(), is(1));
        assertThat(eventSourcesPaths.get(0).toString(), containsString("yaml/event-sources.yaml"));
    }
}