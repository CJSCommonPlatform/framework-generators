package uk.gov.justice.raml.jms.it;

import static com.jayway.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static uk.gov.justice.services.core.annotation.Component.EVENT_LISTENER;

import uk.gov.justice.api.PeopleEventJmsListener;
import uk.gov.justice.api.Service2EventFilter;
import uk.gov.justice.services.adapter.messaging.JmsLoggerMetadataInterceptor;
import uk.gov.justice.services.adapter.messaging.JmsParametersChecker;
import uk.gov.justice.services.adapter.messaging.JmsProcessor;
import uk.gov.justice.services.adapter.messaging.JsonSchemaValidationInterceptor;
import uk.gov.justice.services.common.configuration.ServiceContextNameProvider;
import uk.gov.justice.services.common.converter.ObjectToJsonValueConverter;
import uk.gov.justice.services.common.converter.StringToJsonObjectConverter;
import uk.gov.justice.services.common.converter.jackson.ObjectMapperProducer;
import uk.gov.justice.services.core.accesscontrol.AccessControlFailureMessageGenerator;
import uk.gov.justice.services.core.accesscontrol.AccessControlService;
import uk.gov.justice.services.core.accesscontrol.AllowAllPolicyEvaluator;
import uk.gov.justice.services.core.accesscontrol.PolicyEvaluator;
import uk.gov.justice.services.core.annotation.Handles;
import uk.gov.justice.services.core.annotation.ServiceComponent;
import uk.gov.justice.services.core.cdi.LoggerProducer;
import uk.gov.justice.services.core.dispatcher.DispatcherCache;
import uk.gov.justice.services.core.dispatcher.DispatcherFactory;
import uk.gov.justice.services.core.dispatcher.EmptySystemUserProvider;
import uk.gov.justice.services.core.dispatcher.RequesterProducer;
import uk.gov.justice.services.core.dispatcher.ServiceComponentObserver;
import uk.gov.justice.services.core.dispatcher.SystemUserUtil;
import uk.gov.justice.services.core.enveloper.Enveloper;
import uk.gov.justice.services.core.eventfilter.AllowAllEventFilter;
import uk.gov.justice.services.core.extension.AnnotationScanner;
import uk.gov.justice.services.core.extension.BeanInstantiater;
import uk.gov.justice.services.core.interceptor.InterceptorCache;
import uk.gov.justice.services.core.interceptor.InterceptorChainProcessor;
import uk.gov.justice.services.core.interceptor.InterceptorChainProcessorProducer;
import uk.gov.justice.services.core.interceptor.InterceptorObserver;
import uk.gov.justice.services.core.jms.DefaultJmsDestinations;
import uk.gov.justice.services.core.jms.JmsSenderFactory;
import uk.gov.justice.services.core.json.JsonSchemaLoader;
import uk.gov.justice.services.core.json.JsonSchemaValidator;
import uk.gov.justice.services.core.sender.ComponentDestination;
import uk.gov.justice.services.core.sender.SenderProducer;
import uk.gov.justice.services.event.buffer.EventBufferInterceptor;
import uk.gov.justice.services.event.buffer.api.EventBufferService;
import uk.gov.justice.services.event.filter.EventFilterInterceptor;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.messaging.JsonObjectEnvelopeConverter;
import uk.gov.justice.services.messaging.jms.DefaultJmsEnvelopeSender;
import uk.gov.justice.services.messaging.jms.EnvelopeConverter;
import uk.gov.justice.services.test.utils.common.envelope.TestEnvelopeRecorder;

import java.util.stream.Stream;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jms.Topic;

import org.apache.openejb.jee.WebApp;
import org.apache.openejb.junit.ApplicationComposer;
import org.apache.openejb.testing.Classes;
import org.apache.openejb.testing.Module;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(ApplicationComposer.class)
public class JmsAdapterToHandlerIT extends AbstractJmsAdapterGenerationIT {

    private static final String PEOPLE_EVENT_AA = "people.eventaa";
    @Resource(name = "people.event")
    private Topic peopleEventsDestination;

    @Inject
    private RecordingEventAAHandler aaEventHandler;

    @Inject
    private AllEventsHandler allEventsHandler;

    @Inject
    private RecordingEventBufferService bufferService;

    @Inject
    private RecordingJsonSchemaValidator jsonSchemaValidator;

    @Module
    @Classes(cdi = true, value = {
            PeopleEventJmsListener.class,
            RecordingEventAAHandler.class,
            AllEventsHandler.class,
            RecordingJsonSchemaValidator.class,
            RecordingEventBufferService.class,
            Service2EventFilter.class,

            InterceptorChainProcessorProducer.class,
            InterceptorChainProcessor.class,
            InterceptorCache.class,
            InterceptorObserver.class,
            EventFilterInterceptor.class,
            EventBufferInterceptor.class,

            AnnotationScanner.class,
            RequesterProducer.class,
            ServiceComponentObserver.class,
            JmsProcessor.class,
            SenderProducer.class,
            JmsSenderFactory.class,
            ComponentDestination.class,
            DefaultJmsEnvelopeSender.class,
            DefaultJmsDestinations.class,
            EnvelopeConverter.class,
            JsonSchemaValidationInterceptor.class,
            JmsLoggerMetadataInterceptor.class,
            JmsParametersChecker.class,
            TestServiceContextNameProvider.class,
            JsonSchemaLoader.class,
            StringToJsonObjectConverter.class,
            JsonObjectEnvelopeConverter.class,
            ObjectToJsonValueConverter.class,
            ObjectMapperProducer.class,
            Enveloper.class,
            AccessControlFailureMessageGenerator.class,
            AllowAllPolicyEvaluator.class,
            AccessControlService.class,
            DispatcherCache.class,
            DispatcherFactory.class,
            PolicyEvaluator.class,
            LoggerProducer.class,
            AllowAllEventFilter.class,
            EmptySystemUserProvider.class,
            SystemUserUtil.class,
            BeanInstantiater.class
    })
    public WebApp war() {
        return new WebApp()
                .contextRoot("jms-adapter-to-aaEventHandler-test");
    }

    @Test
    public void shouldProcessSupportedEventThroughJsonValidator_EventBufferAndHandler() throws Exception {
        final String metadataId = "861c9430-7bc6-4bf0-b549-6534394b8d01";
        Thread.sleep(300);

        sendEnvelope(metadataId, PEOPLE_EVENT_AA, peopleEventsDestination);
        await().until(() -> aaEventHandler.recordedEnvelopes().size() > 0);

        assertThat(jsonSchemaValidator.validatedEventName(), is(PEOPLE_EVENT_AA));

        assertThat(bufferService.recordedEnvelopes(), not(empty()));
        assertThat(bufferService.firstRecordedEnvelope().metadata().id().toString(), is(metadataId));

        assertThat(aaEventHandler.recordedEnvelopes(), not(empty()));
        assertThat(aaEventHandler.firstRecordedEnvelope().metadata().id().toString(), is(metadataId));

    }


    @Test
    public void shouldProcessUnSupportedEventThroughEventBufferOnly() throws Exception {
        final String metadataId = "861c9430-7bc6-4bf0-b549-6534394b8d02";
        Thread.sleep(300);

        sendEnvelope(metadataId, "people.unsuported-event", peopleEventsDestination);
        await().until(() -> bufferService.recordedEnvelopes().size() > 0);

        assertThat(bufferService.recordedEnvelopes(), not(empty()));
        assertThat(bufferService.firstRecordedEnvelope().metadata().id().toString(), is(metadataId));

        assertThat(jsonSchemaValidator.validatedEventName(), nullValue());
        assertThat(allEventsHandler.recordedEnvelopes(), empty());

    }


    @ServiceComponent(EVENT_LISTENER)
    @ApplicationScoped
    public static class RecordingEventAAHandler extends TestEnvelopeRecorder {

        @Handles(PEOPLE_EVENT_AA)
        public void handle(final JsonEnvelope envelope) {
            record(envelope);
        }
    }

    @ServiceComponent(EVENT_LISTENER)
    @ApplicationScoped
    public static class AllEventsHandler extends TestEnvelopeRecorder {

        //handles all other events than people.eventaa

        @Handles("*")
        public void handle(final JsonEnvelope envelope) {
            record(envelope);
        }
    }


    @ApplicationScoped
    public static class RecordingEventBufferService extends TestEnvelopeRecorder implements EventBufferService {

        @Override
        public Stream<JsonEnvelope> currentOrderedEventsWith(final JsonEnvelope envelope) {
            record(envelope);
            return Stream.of(envelope);
        }
    }

    @ApplicationScoped
    public static class RecordingJsonSchemaValidator implements JsonSchemaValidator {

        private String validatedEventName;

        @Override
        public void validate(final String payload, final String name) {
            this.validatedEventName = name;
        }

        public String validatedEventName() {
            return validatedEventName;
        }

    }

    @ApplicationScoped
    public static class TestServiceContextNameProvider implements ServiceContextNameProvider {

        @Override
        public String getServiceContextName() {
            return "test-component";
        }
    }
}
