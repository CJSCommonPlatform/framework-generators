package uk.gov.justice.services.clients.messaging.subscription.generator;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static uk.gov.justice.config.GeneratorPropertiesFactory.generatorProperties;
import static uk.gov.justice.services.generators.test.utils.config.GeneratorConfigUtil.configurationWithBasePackage;
import static uk.gov.justice.services.test.utils.core.compiler.JavaCompilerUtility.javaCompilerUtil;
import static uk.gov.justice.services.test.utils.core.reflection.ReflectionUtil.firstMethodOf;
import static uk.gov.justice.services.test.utils.core.reflection.ReflectionUtil.setField;

import uk.gov.justice.services.clients.messaging.subscription.generator.util.SubscriptionWrapperFactory;
import uk.gov.justice.services.generators.subscription.parser.SubscriptionWrapper;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.messaging.jms.JmsEnvelopeSender;
import uk.gov.justice.services.messaging.logging.TraceLogger;

import java.lang.reflect.Method;
import java.util.Optional;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MessagingClientGenerator_MethodBodyTest {

    private static final String BASE_PACKAGE = "org.raml.test";

    @Rule
    public TemporaryFolder outputFolder = new TemporaryFolder();

    @Mock
    private JmsEnvelopeSender sender;

    private final SubscriptionMessagingClientGenerator generator = new SubscriptionMessagingClientGeneratorFactory().create();
    private final SubscriptionWrapperFactory subscriptionWrapperFactory = new SubscriptionWrapperFactory();

    @Test
    public void shouldSendEnvelopeToDestination() throws Exception {

        final SubscriptionWrapper subscriptionWrapper = subscriptionWrapperFactory.createWith("jms:queue:cakeshop.controller.command", "ctx.command.defcmd", "cakeshop", "COMMAND_CONTROLLER");

        generator.run(
                subscriptionWrapper,
                configurationWithBasePackage(BASE_PACKAGE, outputFolder, generatorProperties().withServiceComponentOf("COMMAND_CONTROLLER")));

        final Class<?> generatedClass = javaCompilerUtil().compiledClassOf(
                outputFolder.getRoot(),
                outputFolder.getRoot(),
                BASE_PACKAGE,
                "RemoteCommandController2CakeshopControllerCommandCakeshopMessagingClient");

        final Object instance = instanceOf(generatedClass);
        setField(instance, "traceLogger", mock(TraceLogger.class));

        final JsonEnvelope envelope = mock(JsonEnvelope.class);
        final Optional<Method> method = firstMethodOf(generatedClass);

        assertThat(method.isPresent(), is(true));
        method.get().invoke(instance, envelope);

        verify(sender).send(envelope, "cakeshop.controller.command");
    }

    private Object instanceOf(final Class<?> resourceClass) throws InstantiationException, IllegalAccessException {
        final Object resourceObject = resourceClass.newInstance();
        setField(resourceObject, "sender", sender);
        return resourceObject;
    }
}
