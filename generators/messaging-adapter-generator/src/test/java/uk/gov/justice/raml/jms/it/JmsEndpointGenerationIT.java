package uk.gov.justice.raml.jms.it;

import static javax.json.Json.createObjectBuilder;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import uk.gov.justice.api.ContextaEventProcessorPublicEventJmsListener;
import uk.gov.justice.api.Service1CommandControllerStructureControllerCommandJmsListener;
import uk.gov.justice.api.Service1CommandHandlerStructureHandlerCommandJmsListener;
import uk.gov.justice.api.Service2EventListenerEventFilter;
import uk.gov.justice.api.Service2EventListenerPeopleEventJmsListener;
import uk.gov.justice.api.Service2EventProcessorStructureEventJmsListener;
import uk.gov.justice.services.adapter.messaging.DefaultJmsParameterChecker;
import uk.gov.justice.services.adapter.messaging.DefaultJmsProcessor;
import uk.gov.justice.services.adapter.messaging.JmsLoggerMetadataInterceptor;
import uk.gov.justice.services.adapter.messaging.JsonSchemaValidationInterceptor;
import uk.gov.justice.services.common.configuration.GlobalValueProducer;
import uk.gov.justice.services.common.configuration.ServiceContextNameProvider;
import uk.gov.justice.services.common.converter.ObjectToJsonValueConverter;
import uk.gov.justice.services.common.converter.StringToJsonObjectConverter;
import uk.gov.justice.services.common.converter.jackson.ObjectMapperProducer;
import uk.gov.justice.services.common.util.UtcClock;
import uk.gov.justice.services.components.event.listener.interceptors.EventBufferInterceptor;
import uk.gov.justice.services.components.event.listener.interceptors.EventFilterInterceptor;
import uk.gov.justice.services.core.accesscontrol.AccessControlFailureMessageGenerator;
import uk.gov.justice.services.core.accesscontrol.AllowAllPolicyEvaluator;
import uk.gov.justice.services.core.accesscontrol.DefaultAccessControlService;
import uk.gov.justice.services.core.accesscontrol.PolicyEvaluator;
import uk.gov.justice.services.core.cdi.LoggerProducer;
import uk.gov.justice.services.core.dispatcher.DispatcherCache;
import uk.gov.justice.services.core.dispatcher.EmptySystemUserProvider;
import uk.gov.justice.services.core.dispatcher.ServiceComponentObserver;
import uk.gov.justice.services.core.dispatcher.SystemUserUtil;
import uk.gov.justice.services.core.envelope.EnvelopeValidationExceptionHandlerProducer;
import uk.gov.justice.services.core.enveloper.Enveloper;
import uk.gov.justice.services.core.extension.BeanInstantiater;
import uk.gov.justice.services.core.extension.ServiceComponentScanner;
import uk.gov.justice.services.core.interceptor.InterceptorCache;
import uk.gov.justice.services.core.interceptor.InterceptorChainObserver;
import uk.gov.justice.services.core.interceptor.InterceptorChainProcessor;
import uk.gov.justice.services.core.interceptor.InterceptorChainProcessorProducer;
import uk.gov.justice.services.core.json.DefaultFileSystemUrlResolverStrategy;
import uk.gov.justice.services.core.json.DefaultJsonSchemaValidator;
import uk.gov.justice.services.core.json.DefaultJsonValidationLoggerHelper;

import uk.gov.justice.services.core.json.JsonSchemaLoader;
import uk.gov.justice.services.core.requester.RequesterProducer;
import uk.gov.justice.services.core.sender.SenderProducer;
import uk.gov.justice.services.event.buffer.api.AllowAllEventFilter;
import uk.gov.justice.services.event.buffer.api.EventBufferService;
import uk.gov.justice.services.generators.test.utils.interceptor.RecordingInterceptorChainProcessor;
import uk.gov.justice.services.messaging.DefaultJsonObjectEnvelopeConverter;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.messaging.jms.DefaultEnvelopeConverter;
import uk.gov.justice.services.messaging.jms.DefaultJmsEnvelopeSender;
import uk.gov.justice.services.messaging.logging.DefaultJmsMessageLoggerHelper;
import uk.gov.justice.services.messaging.logging.DefaultTraceLogger;

import java.util.UUID;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Topic;

import org.apache.openejb.jee.WebApp;
import org.apache.openejb.junit.ApplicationComposer;
import org.apache.openejb.testing.Classes;
import org.apache.openejb.testing.Module;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Integration tests for the generated JAX-RS classes.
 */
@RunWith(ApplicationComposer.class)
public class JmsEndpointGenerationIT extends AbstractJmsAdapterGenerationIT {

    @Inject
    RecordingInterceptorChainProcessor interceptorChainProcessor;

    @Resource(name = "structure.handler.command")
    private Queue commandHandlerDestination;

    @Resource(name = "structure.controller.command")
    private Queue commandControllerDestination;

    @Resource(name = "structure.event")
    private Topic structureEventsDestination;

    @Resource(name = "public.event")
    private Topic publicEventsDestination;

    @Module
    @Classes(cdi = true, value = {
            DefaultJmsProcessor.class,
            RecordingInterceptorChainProcessor.class,
            Service1CommandControllerStructureControllerCommandJmsListener.class,
            Service2EventProcessorStructureEventJmsListener.class,
            Service1CommandHandlerStructureHandlerCommandJmsListener.class,
            ContextaEventProcessorPublicEventJmsListener.class,
            ObjectMapperProducer.class,
            DefaultEnvelopeConverter.class,
            StringToJsonObjectConverter.class,
            DefaultJsonObjectEnvelopeConverter.class,
            DefaultJsonSchemaValidator.class,
            JsonSchemaLoader.class,
            LoggerProducer.class,
            AllowAllEventFilter.class,
            DefaultJmsParameterChecker.class,
            TestServiceContextNameProvider.class,
            DefaultJmsMessageLoggerHelper.class,
            DefaultTraceLogger.class,
            DefaultJsonValidationLoggerHelper.class,

            DefaultFileSystemUrlResolverStrategy.class,
            GlobalValueProducer.class,
    })
    public WebApp war() {
        return new WebApp()
                .contextRoot("jms-endpoint-test");
    }


    @Test
    public void commandControllerDispatcherShouldReceiveCommandA() throws JMSException {

        String metadataId = "861c9430-7bc6-4bf0-b549-6534394b8d65";
        String commandName = "structure.commanda";

        sendEnvelope(metadataId, commandName, commandControllerDestination);

        JsonEnvelope receivedEnvelope = interceptorChainProcessor.awaitForEnvelopeWithMetadataOf("id", metadataId);
        assertThat(receivedEnvelope.metadata().id(), is(UUID.fromString(metadataId)));
        assertThat(receivedEnvelope.metadata().name(), is(commandName));
    }

    @Test
    public void commandControllerDispatcherShouldReceiveCommandB() throws JMSException {

        String metadataId = "861c9430-7bc6-4bf0-b549-6534394b8d11";
        String commandName = "structure.commandb";

        sendEnvelope(metadataId, commandName, commandControllerDestination);

        JsonEnvelope receivedEnvelope = interceptorChainProcessor.awaitForEnvelopeWithMetadataOf("id", metadataId);
        assertThat(receivedEnvelope.metadata().name(), is(commandName));
        assertThat(receivedEnvelope.metadata().id(), is(UUID.fromString(metadataId)));
    }

    @Test
    public void commandControllerDispatcherShouldNotReceiveACommandUnspecifiedInMessageSelector()
            throws JMSException, InterruptedException {

        String metadataId = "861c9430-7bc6-4bf0-b549-6534394b8d12";
        String commandName = "structure.commandc";

        sendEnvelope(metadataId, commandName, commandControllerDestination);
        assertTrue(interceptorChainProcessor.notFoundEnvelopeWithMetadataOf("id", metadataId));
    }

    @Test
    public void commandHandlerDispatcherShouldReceiveCommandA() throws JMSException {

        String metadataId = "861c9430-7bc6-4bf0-b549-6534394b8d61";
        String commandName = "structure.cmdaa";

        sendEnvelope(metadataId, commandName, commandHandlerDestination);

        JsonEnvelope receivedEnvelope = interceptorChainProcessor.awaitForEnvelopeWithMetadataOf("id", metadataId);
        assertThat(receivedEnvelope.metadata().id(), is(UUID.fromString(metadataId)));
        assertThat(receivedEnvelope.metadata().name(), is(commandName));
    }

    @Test
    public void commandHandlerDispatcherShouldNotReceiveACommandUnspecifiedInMessageSelector() throws JMSException {

        String metadataId = "861c9430-7bc6-4bf0-b549-6534394b8d13";
        String commandName = "structure.cmdcc";

        sendEnvelope(metadataId, commandName, commandHandlerDestination);
        assertTrue(interceptorChainProcessor.notFoundEnvelopeWithMetadataOf("id", metadataId));
    }

    @Test
    public void dispatcherShouldNotReceiveAMessageNotAdheringToSchema() throws JMSException {

        String metadataId = "961c9430-7bc6-4bf0-b549-6534394b8d13";
        String commandName = "people.create-user";

        sendEnvelope(metadataId, commandName, commandHandlerDestination, createObjectBuilder().add("non_existent_field", "value").build());
        assertTrue(interceptorChainProcessor.notFoundEnvelopeWithMetadataOf("id", metadataId));
    }

    @Test
    public void eventProcessorDispatcherShouldReceiveEvent() throws JMSException, InterruptedException {

        //There's an issue in OpenEJB causing tests that involve JMS topics to fail.
        //On slower machines (e.g. travis) topic consumers tend to be registered after this test starts,
        //which means the message sent to the topic is lost, which in turn causes this test to fail occasionally.
        //Delaying test execution (Thread.sleep) mitigates the issue.
        //TODO: check OpenEJB code and investigate if we can't fix the issue.
        Thread.sleep(300);
        String metadataId = "861c9430-7bc6-4bf0-b549-6534394b8d30";
        String eventName = "structure.eventbb";

        sendEnvelope(metadataId, eventName, structureEventsDestination);

        JsonEnvelope receivedEnvelope = interceptorChainProcessor.awaitForEnvelopeWithMetadataOf("id", metadataId);
        assertThat(receivedEnvelope.metadata().id(), is(UUID.fromString(metadataId)));
        assertThat(receivedEnvelope.metadata().name(), is(eventName));
    }


    @Test
    public void eventListenerDispatcherShouldNotReceiveACommandUnspecifiedInMessageSelector()
            throws JMSException, InterruptedException {

        String metadataId = "861c9430-7bc6-4bf0-b549-6534394b8d21";
        String commandName = "structure.eventcc";

        sendEnvelope(metadataId, commandName, structureEventsDestination);
        assertTrue(interceptorChainProcessor.notFoundEnvelopeWithMetadataOf("id", metadataId));

    }

    @Test
    public void allEventsProcessorDispatcherShouldReceiveAllEvents() throws InterruptedException, JMSException {
        Thread.sleep(300);

        String metadataId1 = "861c9430-7bc6-4bf0-b549-6534394b8d31";
        String eventName1 = "some.eventa";
        sendEnvelope(metadataId1, eventName1, publicEventsDestination);

        String metadataId2 = "861c9430-7bc6-4bf0-b549-6534394b8d32";
        String eventName2 = "other.eventb";
        sendEnvelope(metadataId2, eventName2, publicEventsDestination);

        String metadataId3 = "861c9430-7bc6-4bf0-b549-6534394b8d33";
        String eventName3 = "another.eventc";
        sendEnvelope(metadataId3, eventName3, publicEventsDestination);

        JsonEnvelope receivedEnvelope1 = interceptorChainProcessor.awaitForEnvelopeWithMetadataOf("id", metadataId1);
        assertThat(receivedEnvelope1.metadata().id(), is(UUID.fromString(metadataId1)));
        assertThat(receivedEnvelope1.metadata().name(), is(eventName1));

        JsonEnvelope receivedEnvelope2 = interceptorChainProcessor.awaitForEnvelopeWithMetadataOf("id", metadataId2);
        assertThat(receivedEnvelope2.metadata().id(), is(UUID.fromString(metadataId2)));
        assertThat(receivedEnvelope2.metadata().name(), is(eventName2));

        JsonEnvelope receivedEnvelope3 = interceptorChainProcessor.awaitForEnvelopeWithMetadataOf("id", metadataId3);
        assertThat(receivedEnvelope3.metadata().id(), is(UUID.fromString(metadataId3)));
        assertThat(receivedEnvelope3.metadata().name(), is(eventName3));
    }

    @ApplicationScoped
    public static class TestServiceContextNameProvider implements ServiceContextNameProvider {

        @Override
        public String getServiceContextName() {
            return "test-component";
        }
    }
}
