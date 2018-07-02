package uk.gov.justice.api;

import static java.util.UUID.randomUUID;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static uk.gov.justice.services.core.annotation.Component.COMMAND_API;
import static uk.gov.justice.services.test.utils.core.messaging.JsonEnvelopeBuilder.envelope;
import static uk.gov.justice.services.test.utils.core.messaging.MetadataBuilderFactory.metadataOf;

import uk.gov.justice.schema.catalog.CatalogProducer;
import uk.gov.justice.schema.service.SchemaCatalogService;
import uk.gov.justice.services.common.configuration.GlobalValueProducer;
import uk.gov.justice.services.common.converter.jackson.ObjectMapperProducer;
import uk.gov.justice.services.core.accesscontrol.AccessControlFailureMessageGenerator;
import uk.gov.justice.services.core.accesscontrol.AllowAllPolicyEvaluator;
import uk.gov.justice.services.core.accesscontrol.DefaultAccessControlService;
import uk.gov.justice.services.core.accesscontrol.PolicyEvaluator;
import uk.gov.justice.services.core.annotation.ServiceComponent;
import uk.gov.justice.services.core.cdi.LoggerProducer;
import uk.gov.justice.services.core.dispatcher.DispatcherCache;
import uk.gov.justice.services.core.dispatcher.DispatcherFactory;
import uk.gov.justice.services.core.dispatcher.EnvelopePayloadTypeConverter;
import uk.gov.justice.services.core.dispatcher.JsonEnvelopeRepacker;
import uk.gov.justice.services.core.dispatcher.ServiceComponentObserver;
import uk.gov.justice.services.core.dispatcher.SystemUserProvider;
import uk.gov.justice.services.core.dispatcher.SystemUserUtil;
import uk.gov.justice.services.core.envelope.EnvelopeInspector;
import uk.gov.justice.services.core.envelope.EnvelopeValidationExceptionHandlerProducer;
import uk.gov.justice.services.core.envelope.MediaTypeProvider;
import uk.gov.justice.services.core.extension.BeanInstantiater;
import uk.gov.justice.services.core.interceptor.InterceptorChainProcessor;
import uk.gov.justice.services.core.json.BackwardsCompatibleJsonSchemaValidator;
import uk.gov.justice.services.core.json.DefaultFileSystemUrlResolverStrategy;
import uk.gov.justice.services.core.json.FileBasedJsonSchemaValidator;
import uk.gov.justice.services.core.json.JsonSchemaLoader;
import uk.gov.justice.services.core.json.PayloadExtractor;
import uk.gov.justice.services.core.json.SchemaCatalogAwareJsonSchemaValidator;
import uk.gov.justice.services.core.mapping.ActionNameToMediaTypesMappingObserver;
import uk.gov.justice.services.core.mapping.DefaultMediaTypesMappingCache;
import uk.gov.justice.services.core.mapping.DefaultNameToMediaTypeConverter;
import uk.gov.justice.services.core.mapping.DefaultSchemaIdMappingCache;
import uk.gov.justice.services.core.mapping.MediaTypesMappingCacheInitialiser;
import uk.gov.justice.services.core.mapping.SchemaIdMappingCacheInitialiser;
import uk.gov.justice.services.core.mapping.SchemaIdMappingObserver;
import uk.gov.justice.services.core.requester.RequesterProducer;
import uk.gov.justice.services.core.sender.Sender;
import uk.gov.justice.services.core.sender.SenderProducer;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.messaging.logging.DefaultTraceLogger;
import uk.gov.justice.test.util.RecordingJmsEnvelopeSender;

import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.openejb.OpenEjbContainer;
import org.apache.openejb.jee.WebApp;
import org.apache.openejb.junit.ApplicationComposer;
import org.apache.openejb.testing.Classes;
import org.apache.openejb.testing.Configuration;
import org.apache.openejb.testing.Module;
import org.apache.openejb.testng.PropertiesBuilder;
import org.apache.openejb.util.NetworkUtil;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ApplicationComposer.class)
@ServiceComponent(COMMAND_API)
public class RemoteCommandControllerIT {

    private static final UUID TEST_SYS_USER_ID = randomUUID();
    private static int port = -1;

    @Inject
    Sender sender;

    @Inject
    RecordingJmsEnvelopeSender envelopeSender;
    private static final String COMMAND_NAME = "contexta.commanda";

    @BeforeClass
    public static void beforeClass() {
        port = NetworkUtil.getNextAvailablePort();
    }

    @Module
    @Classes(cdi = true, value = {
            AccessControlFailureMessageGenerator.class,
            DefaultAccessControlService.class,
            AllowAllPolicyEvaluator.class,
            InterceptorChainProcessor.class,
            DispatcherCache.class,
            DispatcherFactory.class,
            EnvelopePayloadTypeConverter.class,
            JsonEnvelopeRepacker.class,
            PolicyEvaluator.class,
            RecordingJmsEnvelopeSender.class,
            RemoteCommandApi2CommandControllerMessageService1ContextaControllerCommand.class,
            RequesterProducer.class,
            SenderProducer.class,
            ServiceComponentObserver.class,
            LoggerProducer.class,
            TestSystemUserProvider.class,
            SystemUserUtil.class,
            BeanInstantiater.class,
            GlobalValueProducer.class,
            EnvelopeValidationExceptionHandlerProducer.class,
            FileBasedJsonSchemaValidator.class,
            JsonSchemaLoader.class,
            ObjectMapperProducer.class,
            DefaultTraceLogger.class,

            DefaultFileSystemUrlResolverStrategy.class,

            SchemaCatalogAwareJsonSchemaValidator.class,
            PayloadExtractor.class,
            DefaultNameToMediaTypeConverter.class,
            DefaultSchemaIdMappingCache.class,
            SchemaIdMappingObserver.class,

            CatalogProducer.class,
            SchemaCatalogService.class,

            DefaultMediaTypesMappingCache.class,
            ActionNameToMediaTypesMappingObserver.class,
            MediaTypeProvider.class,
            BackwardsCompatibleJsonSchemaValidator.class,
            EnvelopeInspector.class,

            MediaTypesMappingCacheInitialiser.class,
            SchemaIdMappingCacheInitialiser.class
    })
    public WebApp war() {
        return new WebApp()
                .contextRoot("RemoteCommandControllerIT");
    }

    @Configuration
    public Properties properties() {
        return new PropertiesBuilder()
                .property("httpejbd.port", Integer.toString(port))
                .property(OpenEjbContainer.OPENEJB_EMBEDDED_REMOTABLE, "true")
                .build();
    }

    @Before
    public void setUp() {
        envelopeSender.init();
    }

    @Test
    public void shouldPassEnvelopeToEnvelopeSender() {
        final UUID id = randomUUID();
        final String userId = "userId1234";
        sender.send(envelope()
                .with(metadataOf(id, COMMAND_NAME).withUserId(userId))
                .withPayloadOf("aa", "someField1")
                .build());

        final List<JsonEnvelope> sentEnvelopes = envelopeSender.envelopesSentTo("contexta.controller.command");
        assertThat(sentEnvelopes, hasSize(1));
        assertThat(sentEnvelopes.get(0).metadata().name(), is(COMMAND_NAME));
        assertThat(sentEnvelopes.get(0).metadata().id(), is(id));
        assertThat(sentEnvelopes.get(0).metadata().userId().get(), is(userId));
    }

    @Test
    public void shouldPassEnvelopeWithSystemUserIdToEnvelopeSender() {
        final UUID id = randomUUID();
        final String userId = "userId1235";
        sender.sendAsAdmin(envelope()
                .with(metadataOf(id, COMMAND_NAME).withUserId(userId))
                .withPayloadOf("aa", "someField1")
                .build());

        final List<JsonEnvelope> sentEnvelopes = envelopeSender.envelopesSentTo("contexta.controller.command");
        assertThat(sentEnvelopes, hasSize(1));
        assertThat(sentEnvelopes.get(0).metadata().name(), is(COMMAND_NAME));
        assertThat(sentEnvelopes.get(0).metadata().id(), is(id));
        assertThat(sentEnvelopes.get(0).metadata().userId().get(), is(TEST_SYS_USER_ID.toString()));
    }

    @ApplicationScoped
    public static class TestSystemUserProvider implements SystemUserProvider {

        @Override
        public Optional<UUID> getContextSystemUserId() {
            return Optional.of(TEST_SYS_USER_ID);
        }
    }
}
