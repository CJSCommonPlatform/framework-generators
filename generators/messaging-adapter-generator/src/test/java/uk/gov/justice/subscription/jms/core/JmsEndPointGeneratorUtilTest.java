package uk.gov.justice.subscription.jms.core;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static uk.gov.justice.subscription.domain.builders.EventBuilder.event;
import static uk.gov.justice.subscription.jms.core.JmsEndPointGeneratorUtil.shouldGenerateEventFilter;
import static uk.gov.justice.subscription.jms.core.JmsEndPointGeneratorUtil.shouldListenToAllMessages;

import uk.gov.justice.subscription.domain.subscriptiondescriptor.Event;

import org.junit.Test;

public class JmsEndPointGeneratorUtilTest {

    @Test
    public void shouldGenerateEventFilterForEventListenerCheck() {

        final Event event = event()
                .withName("my-context.events.something-happened")
                .withSchemaUri("http://justice.gov.uk/json/schemas/domains/example/my-context.events.something-happened.json")
                .build();

        boolean shouldGenerateEventFilter = shouldGenerateEventFilter(asList(event), "EVENT_LISTENER");

        assertThat(shouldGenerateEventFilter, is(true));
    }

    @Test
    public void shouldGenerateEventFilterForEventIndexerCheck() {

        final Event event = event()
                .withName("my-context.events.something-happened")
                .withSchemaUri("http://justice.gov.uk/json/schemas/domains/example/my-context.events.something-happened.json")
                .build();

        boolean shouldGenerateEventFilter = shouldGenerateEventFilter(asList(event), "EVENT_INDEXER");

        assertThat(shouldGenerateEventFilter, is(true));
    }

    @Test
    public void shouldListenToAllMessagesCheckForEventListener() {

        final Event event = event()
                .withName("my-context.events.something-happened")
                .withSchemaUri("http://justice.gov.uk/json/schemas/domains/example/my-context.events.something-happened.json")
                .build();

        boolean shouldGenerateEventFilter = shouldListenToAllMessages(asList(event), "EVENT_LISTENER");

        assertThat(shouldGenerateEventFilter, is(true));
    }


    @Test
    public void shouldListenToAllMessagesCheckForEventIndexer() {

        final Event event = event()
                .withName("my-context.events.something-happened")
                .withSchemaUri("http://justice.gov.uk/json/schemas/domains/example/my-context.events.something-happened.json")
                .build();

        boolean shouldGenerateEventFilter = shouldListenToAllMessages(asList(event), "EVENT_INDEXER");

        assertThat(shouldGenerateEventFilter, is(true));
    }
}
