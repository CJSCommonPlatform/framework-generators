package uk.gov.justice.subscription.jms.it;

import static java.util.UUID.randomUUID;
import static javax.json.Json.createObjectBuilder;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static uk.gov.justice.services.core.annotation.Component.EVENT_LISTENER;
import static uk.gov.justice.services.core.interceptor.InterceptorContext.interceptorContextWithInput;
import static uk.gov.justice.services.messaging.JsonEnvelope.envelopeFrom;
import static uk.gov.justice.services.messaging.JsonEnvelope.metadataBuilder;

import uk.gov.justice.api.subscription.Service2EventListenerAnotherPeopleEventEventFilter;
import uk.gov.justice.api.subscription.Service2EventListenerAnotherPeopleEventEventFilterInterceptor;
import uk.gov.justice.api.subscription.Service2EventListenerAnotherPeopleEventEventInterceptorChainProvider;
import uk.gov.justice.services.adapter.messaging.DefaultJmsParameterChecker;
import uk.gov.justice.services.cdi.LoggerProducer;
import uk.gov.justice.services.common.annotation.ComponentNameExtractor;
import uk.gov.justice.services.common.converter.jackson.ObjectMapperProducer;
import uk.gov.justice.services.core.annotation.Adapter;
import uk.gov.justice.services.core.annotation.Handles;
import uk.gov.justice.services.core.annotation.ServiceComponent;
import uk.gov.justice.services.core.dispatcher.DispatcherCache;
import uk.gov.justice.services.core.dispatcher.DispatcherFactory;
import uk.gov.justice.services.core.dispatcher.EnvelopePayloadTypeConverter;
import uk.gov.justice.services.core.dispatcher.JsonEnvelopeRepacker;
import uk.gov.justice.services.core.dispatcher.ServiceComponentObserver;
import uk.gov.justice.services.core.extension.BeanInstantiater;
import uk.gov.justice.services.core.interceptor.InterceptorCache;
import uk.gov.justice.services.core.interceptor.InterceptorChainObserver;
import uk.gov.justice.services.core.interceptor.InterceptorChainProcessor;
import uk.gov.justice.services.core.interceptor.InterceptorChainProcessorProducer;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.test.utils.common.envelope.TestEnvelopeRecorder;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.openejb.jee.WebApp;
import org.apache.openejb.junit.ApplicationComposer;
import org.apache.openejb.testing.Classes;
import org.apache.openejb.testing.Module;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(ApplicationComposer.class)
@Adapter("EVENT_LISTENER")
public class FilterInterceptorIT {

    private static final String PEOPLE_EVENT_AA = "people.eventaa";

    @Inject
    private RecordingEventAAHandler aaEventHandler;

    @Inject
    private InterceptorChainProcessor interceptorChainProcessor;

    @Module
    @Classes(cdi = true, value = {
            Service2EventListenerAnotherPeopleEventEventFilter.class,
            Service2EventListenerAnotherPeopleEventEventFilterInterceptor.class,
            Service2EventListenerAnotherPeopleEventEventInterceptorChainProvider.class,

            RecordingEventAAHandler.class,

            InterceptorChainProcessorProducer.class,
            InterceptorChainProcessor.class,
            InterceptorCache.class,
            InterceptorChainObserver.class,

            DispatcherCache.class,
            DispatcherFactory.class,
            JsonEnvelopeRepacker.class,
            EnvelopePayloadTypeConverter.class,
            LoggerProducer.class,
            BeanInstantiater.class,
            ObjectMapperProducer.class,
            DefaultJmsParameterChecker.class,
            ServiceComponentObserver.class,
            ComponentNameExtractor.class
    })
    public WebApp war() {
        return new WebApp()
                .contextRoot("subscription.JmsAdapterToHandlerIT");
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void shouldHandleSupportedEvent() throws Exception {

        final UUID messageId = randomUUID();
        final UUID streamId = randomUUID();
        final String messageStr = "textMessage";

        final JsonEnvelope jsonEnvelope = envelopeFrom(
                metadataBuilder()
                        .withId(messageId)
                        .withName(PEOPLE_EVENT_AA)
                        .withStreamId(streamId),
                createObjectBuilder()
                        .add("message", messageStr));

        interceptorChainProcessor.process(interceptorContextWithInput(jsonEnvelope));

        assertThat(aaEventHandler.recordedEnvelopes().size(), is(1));
        assertThat(aaEventHandler.firstRecordedEnvelope().metadata().id(), is(messageId));
        assertThat(aaEventHandler.firstRecordedEnvelope().metadata().streamId().get(), is(streamId));
        assertThat(aaEventHandler.firstRecordedEnvelope().metadata().name(), is(PEOPLE_EVENT_AA));
    }

    @Test
    public void shouldNotHandleUnsupportedEvent() throws Exception {

        final UUID messageId = randomUUID();
        final UUID streamId = randomUUID();
        final String messageStr = "textMessage";

        final JsonEnvelope jsonEnvelope = envelopeFrom(
                metadataBuilder()
                        .withId(messageId)
                        .withName("people.unsuported-event")
                        .withStreamId(streamId),
                createObjectBuilder()
                        .add("message", messageStr));

        interceptorChainProcessor.process(interceptorContextWithInput(jsonEnvelope));

        assertThat(aaEventHandler.recordedEnvelopes().size(), is(0));
    }

    @ServiceComponent(EVENT_LISTENER)
    @ApplicationScoped
    public static class RecordingEventAAHandler extends TestEnvelopeRecorder {

        @Handles(PEOPLE_EVENT_AA)
        public void handle(final JsonEnvelope envelope) {
            record(envelope);
        }
    }
}
