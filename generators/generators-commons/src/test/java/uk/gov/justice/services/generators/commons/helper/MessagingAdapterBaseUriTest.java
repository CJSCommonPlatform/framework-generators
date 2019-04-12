package uk.gov.justice.services.generators.commons.helper;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static uk.gov.justice.services.core.annotation.Component.EVENT_INDEXER;
import static uk.gov.justice.services.core.annotation.Component.EVENT_LISTENER;
import static uk.gov.justice.services.core.annotation.Component.EVENT_PROCESSOR;

import org.junit.Test;

public class MessagingAdapterBaseUriTest {
    @Test
    public void shouldReturnTier() {
        assertThat(new MessagingAdapterBaseUri("message://event/listener/message/service1").tier(), is("listener"));
        assertThat(new MessagingAdapterBaseUri("message://event/processor/message/service2").tier(), is("processor"));
        assertThat(new MessagingAdapterBaseUri("message://event/indexer/message/service3").tier(), is("indexer"));
    }

    @Test
    public void shouldReturnPillar() {
        assertThat(new MessagingAdapterBaseUri("message://event/listener/message/service1").pillar(), is("event"));
        assertThat(new MessagingAdapterBaseUri("message://event/indexer/message/service3").pillar(), is("event"));
        assertThat(new MessagingAdapterBaseUri("message://command/handler/message/service2").pillar(), is("command"));
    }

    @Test
    public void shouldReturnService() {
        assertThat(new MessagingAdapterBaseUri("message://event/listener/message/service1").service(), is("service1"));
        assertThat(new MessagingAdapterBaseUri("message://event/indexer/message/service3").service(), is("service3"));
        assertThat(new MessagingAdapterBaseUri("message://command/handler/message/service2").service(), is("service2"));
    }

    @Test
    public void shouldReturnAdapterClientId() {
        assertThat(new MessagingAdapterBaseUri("message://event/listener/message/service1").adapterClientId(), is("service1.event.listener"));
        assertThat(new MessagingAdapterBaseUri("message://event/processor/message/service2").adapterClientId(), is("service2.event.processor"));
        assertThat(new MessagingAdapterBaseUri("message://event/indexer/message/service3").adapterClientId(), is("service3.event.indexer"));
    }

    @Test
    public void shouldReturnTrueIfValidBaseUri() {
        assertTrue(MessagingAdapterBaseUri.valid("message://event/listener/message/service1"));
        assertTrue(MessagingAdapterBaseUri.valid("message://event/processor/message/service2"));
        assertTrue(MessagingAdapterBaseUri.valid("message://event/indexer/message/service3"));
        assertTrue(MessagingAdapterBaseUri.valid("message://command/controller/message/service2"));
    }

    @Test
    public void shouldReturnTrueIfNotValidBaseUri() {
        assertFalse(MessagingAdapterBaseUri.valid("message://INVALID/listener/message/service1"));
        assertFalse(MessagingAdapterBaseUri.valid("message://INVALID/indexer/message/service1"));
        assertFalse(MessagingAdapterBaseUri.valid("message://event/INVALID/message/service2"));
        assertFalse(MessagingAdapterBaseUri.valid("message://command/controller/message"));
    }


    @Test
    public void shouldReturnComponent() {
        assertThat(new MessagingAdapterBaseUri("message://event/listener/message/service1").component(), is(EVENT_LISTENER));
        assertThat(new MessagingAdapterBaseUri("message://event/processor/message/service2").component(), is(EVENT_PROCESSOR));
        assertThat(new MessagingAdapterBaseUri("message://event/indexer/message/service2").component(), is(EVENT_INDEXER));
    }

    @Test
    public void shouldReturnClassName() {
        assertThat(new MessagingAdapterBaseUri("message://event/listener/message/system").toClassName(), is("SystemEventListener"));
    }
}
