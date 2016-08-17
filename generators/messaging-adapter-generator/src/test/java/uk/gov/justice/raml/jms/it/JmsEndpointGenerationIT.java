package uk.gov.justice.raml.jms.it;

import static javax.json.Json.createObjectBuilder;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import uk.gov.justice.api.PublicEventJmsListener;
import uk.gov.justice.api.StructureControllerCommandJmsListener;
import uk.gov.justice.api.StructureEventJmsListener;
import uk.gov.justice.api.StructureHandlerCommandJmsListener;
import uk.gov.justice.services.adapter.messaging.JmsProcessor;
import uk.gov.justice.services.common.converter.StringToJsonObjectConverter;
import uk.gov.justice.services.common.converter.jackson.ObjectMapperProducer;
import uk.gov.justice.services.core.cdi.LoggerProducer;
import uk.gov.justice.services.core.json.JsonSchemaLoader;
import uk.gov.justice.services.core.json.JsonSchemaValidator;
import uk.gov.justice.services.generators.test.utils.dispatcher.AsynchronousRecordingDispatcher;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.messaging.JsonObjectEnvelopeConverter;
import uk.gov.justice.services.messaging.JsonObjects;
import uk.gov.justice.services.messaging.jms.EnvelopeConverter;

import java.util.Properties;
import java.util.UUID;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.json.JsonObject;
import javax.naming.NamingException;

import org.apache.cxf.common.i18n.Exception;
import org.apache.openejb.OpenEjbContainer;
import org.apache.openejb.jee.WebApp;
import org.apache.openejb.junit.ApplicationComposer;
import org.apache.openejb.testing.Classes;
import org.apache.openejb.testing.Configuration;
import org.apache.openejb.testing.Module;
import org.apache.openejb.testng.PropertiesBuilder;
import org.apache.openejb.util.NetworkUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Integration tests for the generated JAX-RS classes.
 */
@RunWith(ApplicationComposer.class)
public class JmsEndpointGenerationIT {

    private static int port = -1;

    @Inject
    AsynchronousRecordingDispatcher dispatcher;

    @Resource(name = "structure.handler.command")
    private Queue commandHandlerDestination;

    @Resource(name = "structure.controller.command")
    private Queue commandControllerDestination;

    @Resource(name = "structure.event")
    private Topic eventsDestination;

    @Resource(name = "public.event")
    private Topic publicEventsDestination;

    @Resource
    private ConnectionFactory factory;

    private Connection connection;

    private Session session;

    @BeforeClass
    public static void beforeClass() {
        port = NetworkUtil.getNextAvailablePort();
    }

    @Configuration
    public Properties properties() {
        return new PropertiesBuilder()
                .property("httpejbd.port", Integer.toString(port))
                .property(OpenEjbContainer.OPENEJB_EMBEDDED_REMOTABLE, "true")
                .build();
    }

    @Module
    @Classes(cdi = true, value = {
            JmsProcessor.class,
            AsynchronousRecordingDispatcher.class,
            StructureControllerCommandJmsListener.class,
            StructureEventJmsListener.class,
            StructureHandlerCommandJmsListener.class,
            PublicEventJmsListener.class,
            ObjectMapperProducer.class,
            EnvelopeConverter.class,
            StringToJsonObjectConverter.class,
            JsonObjectEnvelopeConverter.class,
            JsonSchemaValidator.class,
            JsonSchemaLoader.class,
            LoggerProducer.class
    })
    public WebApp war() {
        return new WebApp()
                .contextRoot("jms-endpoint-test");
    }

    @Before
    public void setup() throws Exception, JMSException, NamingException {
        connection = factory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    @After
    public void after() throws JMSException {
        connection.close();
        session.close();
    }


    @Test
    public void commandControllerDispatcherShouldReceiveCommandA() throws JMSException {

        String metadataId = "861c9430-7bc6-4bf0-b549-6534394b8d65";
        String commandName = "structure.commanda";

        sendEnvelope(metadataId, commandName, commandControllerDestination);

        JsonEnvelope receivedEnvelope = dispatcher.awaitForEnvelopeWithMetadataOf("id", metadataId);
        assertThat(receivedEnvelope.metadata().id(), is(UUID.fromString(metadataId)));
        assertThat(receivedEnvelope.metadata().name(), is(commandName));
    }

    @Test
    public void commandControllerDispatcherShouldReceiveCommandB() throws JMSException {

        String metadataId = "861c9430-7bc6-4bf0-b549-6534394b8d11";
        String commandName = "structure.commandb";

        sendEnvelope(metadataId, commandName, commandControllerDestination);

        JsonEnvelope receivedEnvelope = dispatcher.awaitForEnvelopeWithMetadataOf("id", metadataId);
        assertThat(receivedEnvelope.metadata().name(), is(commandName));
        assertThat(receivedEnvelope.metadata().id(), is(UUID.fromString(metadataId)));
    }

    @Test
    public void commandControllerDispatcherShouldNotReceiveACommandUnspecifiedInMessageSelector()
            throws JMSException, InterruptedException {

        String metadataId = "861c9430-7bc6-4bf0-b549-6534394b8d12";
        String commandName = "structure.commandc";

        sendEnvelope(metadataId, commandName, commandControllerDestination);
        assertTrue(dispatcher.notFoundEnvelopeWithMetadataOf("id", metadataId));
    }

    @Test
    public void commandHandlerDispatcherShouldReceiveCommandA() throws JMSException {

        String metadataId = "861c9430-7bc6-4bf0-b549-6534394b8d61";
        String commandName = "structure.cmdaa";

        sendEnvelope(metadataId, commandName, commandHandlerDestination);

        JsonEnvelope receivedEnvelope = dispatcher.awaitForEnvelopeWithMetadataOf("id", metadataId);
        assertThat(receivedEnvelope.metadata().id(), is(UUID.fromString(metadataId)));
        assertThat(receivedEnvelope.metadata().name(), is(commandName));
    }

    @Test
    public void commandHandlerDispatcherShouldNotReceiveACommandUnspecifiedInMessageSelector() throws JMSException {

        String metadataId = "861c9430-7bc6-4bf0-b549-6534394b8d13";
        String commandName = "structure.cmdcc";

        sendEnvelope(metadataId, commandName, commandHandlerDestination);
        assertTrue(dispatcher.notFoundEnvelopeWithMetadataOf("id", metadataId));
    }

    @Test
    public void dispatcherShouldNotReceiveAMessageNotAdheringToSchema() throws JMSException {

        String metadataId = "961c9430-7bc6-4bf0-b549-6534394b8d13";
        String commandName = "people.create-user";

        sendEnvelope(metadataId, commandName, commandHandlerDestination, createObjectBuilder().add("non_existent_field", "value").build());
        assertTrue(dispatcher.notFoundEnvelopeWithMetadataOf("id", metadataId));
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

        sendEnvelope(metadataId, eventName, eventsDestination);

        JsonEnvelope receivedEnvelope = dispatcher.awaitForEnvelopeWithMetadataOf("id", metadataId);
        assertThat(receivedEnvelope.metadata().id(), is(UUID.fromString(metadataId)));
        assertThat(receivedEnvelope.metadata().name(), is(eventName));
    }


    @Test
    public void eventListenerDispatcherShouldNotReceiveACommandUnspecifiedInMessageSelector()
            throws JMSException, InterruptedException {

        String metadataId = "861c9430-7bc6-4bf0-b549-6534394b8d21";
        String commandName = "structure.eventcc";

        sendEnvelope(metadataId, commandName, eventsDestination);
        assertTrue(dispatcher.notFoundEnvelopeWithMetadataOf("id", metadataId));

    }

    @Test
    public void allEventsListenerDispatcherShouldReceiveAllEvents() throws InterruptedException, JMSException {
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

        JsonEnvelope receivedEnvelope1 = dispatcher.awaitForEnvelopeWithMetadataOf("id", metadataId1);
        assertThat(receivedEnvelope1.metadata().id(), is(UUID.fromString(metadataId1)));
        assertThat(receivedEnvelope1.metadata().name(), is(eventName1));

        JsonEnvelope receivedEnvelope2 = dispatcher.awaitForEnvelopeWithMetadataOf("id", metadataId2);
        assertThat(receivedEnvelope2.metadata().id(), is(UUID.fromString(metadataId2)));
        assertThat(receivedEnvelope2.metadata().name(), is(eventName2));

        JsonEnvelope receivedEnvelope3 = dispatcher.awaitForEnvelopeWithMetadataOf("id", metadataId3);
        assertThat(receivedEnvelope3.metadata().id(), is(UUID.fromString(metadataId3)));
        assertThat(receivedEnvelope3.metadata().name(), is(eventName3));
    }

    private void sendEnvelope(String metadataId, String commandName, Destination queue) throws JMSException {
        sendEnvelope(metadataId, commandName, queue, createObjectBuilder().build());
    }

    private void sendEnvelope(String metadataId, String commandName, Destination queue, JsonObject payload) throws JMSException {
        TextMessage message = session.createTextMessage();
        message.setText(envelopeJsonWith(metadataId, commandName, payload));
        message.setStringProperty("CPPNAME", commandName);
        try (MessageProducer producer = session.createProducer(queue)) {
            producer.send(message);
        }
    }

    private String envelopeJsonWith(String metadataId, String commandName, JsonObject payload) {
        return JsonObjects.createObjectBuilder(payload)
                .add("_metadata", createObjectBuilder()
                        .add("id", metadataId)
                        .add("name", commandName))
                .build().toString();
    }
}
