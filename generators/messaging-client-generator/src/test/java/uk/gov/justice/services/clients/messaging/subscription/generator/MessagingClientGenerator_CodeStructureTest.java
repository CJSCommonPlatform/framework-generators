package uk.gov.justice.services.clients.messaging.subscription.generator;


import static java.lang.reflect.Modifier.isFinal;
import static java.lang.reflect.Modifier.isPrivate;
import static java.lang.reflect.Modifier.isStatic;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static uk.gov.justice.config.GeneratorPropertiesFactory.generatorProperties;
import static uk.gov.justice.services.generators.test.utils.config.GeneratorConfigUtil.configurationWithBasePackage;
import static uk.gov.justice.services.test.utils.core.compiler.JavaCompilerUtility.javaCompilerUtil;
import static uk.gov.justice.services.test.utils.core.reflection.ReflectionUtil.firstMethodOf;
import static uk.gov.justice.services.test.utils.core.reflection.ReflectionUtil.methodsOf;

import uk.gov.justice.services.clients.messaging.subscription.generator.util.SubscriptionWrapperFactory;
import uk.gov.justice.services.core.annotation.FrameworkComponent;
import uk.gov.justice.services.core.annotation.Handles;
import uk.gov.justice.services.core.annotation.Remote;
import uk.gov.justice.services.generators.subscription.parser.SubscriptionWrapper;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.messaging.jms.JmsEnvelopeSender;
import uk.gov.justice.services.test.utils.core.compiler.JavaCompilerUtility;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import javax.inject.Inject;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;

public class MessagingClientGenerator_CodeStructureTest {

    private static final String BASE_PACKAGE = "org.raml.test";
    private static final JavaCompilerUtility COMPILER = javaCompilerUtil();

    private final SubscriptionMessagingClientGenerator generator = new SubscriptionMessagingClientGeneratorFactory().create();
    private final SubscriptionWrapperFactory subscriptionWrapperFactory = new SubscriptionWrapperFactory();

    @Rule
    public TemporaryFolder outputFolder = new TemporaryFolder();

    @Test
    public void shouldGenerateClassWithAnnotations() throws Exception {

        final SubscriptionWrapper subscriptionWrapper = subscriptionWrapperFactory.createWith("jms:topic:public.event", "some event", "cakeshop", "COMMAND_API");

        generator.run(
                subscriptionWrapper,
                configurationWithBasePackage(BASE_PACKAGE, outputFolder, generatorProperties().withServiceComponentOf("COMMAND_API")));

        final Class<?> generatedClass = COMPILER.compiledClassOf(
                outputFolder.getRoot(),
                outputFolder.getRoot(),
                BASE_PACKAGE,
                "RemoteCommandApi2PublicEventCakeshopMessagingClient");

        assertThat(generatedClass.getCanonicalName(), is("org.raml.test.RemoteCommandApi2PublicEventCakeshopMessagingClient"));
        assertThat(generatedClass.getAnnotation(Remote.class), not(nullValue()));
        assertThat(generatedClass.getAnnotation(FrameworkComponent.class), not(nullValue()));
        assertThat(generatedClass.getAnnotation(FrameworkComponent.class).value(), is("COMMAND_API"));

    }

    @Test
    public void shouldGenerateClassWithCommandControllerAnnotation() throws Exception {

        final SubscriptionWrapper subscriptionWrapper = subscriptionWrapperFactory.createWith("jms:topic:public.event", "some event", "cakeshop", "COMMAND_CONTROLLER");

        generator.run(
                subscriptionWrapper,
                configurationWithBasePackage(BASE_PACKAGE, outputFolder, generatorProperties().withServiceComponentOf("COMMAND_CONTROLLER")));

        final Class<?> generatedClass = COMPILER.compiledClassOf(
                outputFolder.getRoot(),
                outputFolder.getRoot(),
                BASE_PACKAGE,
                "RemoteCommandController2PublicEventCakeshopMessagingClient");

        assertThat(generatedClass.getAnnotation(FrameworkComponent.class).value(), is("COMMAND_CONTROLLER"));
    }

    @Test
    public void shouldGenerateClientForEventTopic() throws Exception {

        final SubscriptionWrapper subscriptionWrapper = subscriptionWrapperFactory.createWith("jms:topic:public.event", "some event", "cakeshop", "EVENT_PROCESSOR");

        generator.run(
                subscriptionWrapper,
                configurationWithBasePackage(BASE_PACKAGE, outputFolder, generatorProperties().withServiceComponentOf("EVENT_PROCESSOR")));

        final Class<?> generatedClass = COMPILER.compiledClassOf(
                outputFolder.getRoot(),
                outputFolder.getRoot(),
                BASE_PACKAGE,
                "RemoteEventProcessor2PublicEventCakeshopMessagingClient");

        assertThat(generatedClass.getAnnotation(FrameworkComponent.class).value(), is("EVENT_PROCESSOR"));
    }

    @Test
    public void shouldCreateLoggerConstant() throws Exception {

        final SubscriptionWrapper subscriptionWrapper = subscriptionWrapperFactory.createWith("jms:topic:public.event", "some event", "cakeshop", "COMMAND_CONTROLLER");

        generator.run(
                subscriptionWrapper,
                configurationWithBasePackage(BASE_PACKAGE, outputFolder, generatorProperties().withServiceComponentOf("COMMAND_CONTROLLER")));

        final Class<?> generatedClass = COMPILER.compiledClassOf(
                outputFolder.getRoot(),
                outputFolder.getRoot(),
                BASE_PACKAGE,
                "RemoteCommandController2PublicEventCakeshopMessagingClient");

        final Field logger = generatedClass.getDeclaredField("LOGGER");
        assertThat(logger, not(nullValue()));
        assertThat(logger.getType(), equalTo(Logger.class));
        assertThat(isPrivate(logger.getModifiers()), is(true));
        assertThat(isStatic(logger.getModifiers()), is(true));
        assertThat(isFinal(logger.getModifiers()), is(true));
    }

    @Test
    public void shouldCreateJmsEnvelopeSenderVariable() throws Exception {

        final SubscriptionWrapper subscriptionWrapper = subscriptionWrapperFactory.createWith("jms:topic:public.event", "some event", "cakeshop", "COMMAND_CONTROLLER");

        generator.run(
                subscriptionWrapper,
                configurationWithBasePackage(BASE_PACKAGE, outputFolder, generatorProperties().withServiceComponentOf("COMMAND_CONTROLLER")));

        final Class<?> generatedClass = COMPILER.compiledClassOf(
                outputFolder.getRoot(),
                outputFolder.getRoot(),
                BASE_PACKAGE,
                "RemoteCommandController2PublicEventCakeshopMessagingClient");

        final Field sender = generatedClass.getDeclaredField("sender");
        assertThat(sender, not(nullValue()));
        assertThat(sender.getAnnotation(Inject.class), not(nullValue()));
        assertThat(sender.getType(), equalTo(JmsEnvelopeSender.class));
        assertThat(isStatic(sender.getModifiers()), is(false));

    }

    @Test
    public void shouldGenerateMethodAnnotatedWithHandlesAnnotation() throws Exception {

        final SubscriptionWrapper subscriptionWrapper = subscriptionWrapperFactory.createWith("jms:topic:public.event", "cakeshop.actionabc", "cakeshop", "COMMAND_CONTROLLER");

        generator.run(
                subscriptionWrapper,
                configurationWithBasePackage(BASE_PACKAGE, outputFolder, generatorProperties().withServiceComponentOf("COMMAND_CONTROLLER")));

        final Class<?> generatedClass = COMPILER.compiledClassOf(
                outputFolder.getRoot(),
                outputFolder.getRoot(),
                BASE_PACKAGE,
                "RemoteCommandController2PublicEventCakeshopMessagingClient");

        List<Method> methods = methodsOf(generatedClass);
        assertThat(methods, hasSize(1));

        final Method method = methods.get(0);
        final Handles handlesAnnotation = method.getAnnotation(Handles.class);
        assertThat(handlesAnnotation, not(nullValue()));
        assertThat(handlesAnnotation.value(), is("cakeshop.actionabc"));
    }

    @Test
    public void shouldGenerateMethodAcceptingEnvelope() throws Exception {

        final SubscriptionWrapper subscriptionWrapper = subscriptionWrapperFactory.createWith("jms:topic:public.event", "cakeshop.actionabc", "cakeshop", "COMMAND_CONTROLLER");

        generator.run(
                subscriptionWrapper,
                configurationWithBasePackage(BASE_PACKAGE, outputFolder, generatorProperties().withServiceComponentOf("COMMAND_CONTROLLER")));

        final Class<?> generatedClass = COMPILER.compiledClassOf(
                outputFolder.getRoot(),
                outputFolder.getRoot(),
                BASE_PACKAGE,
                "RemoteCommandController2PublicEventCakeshopMessagingClient");

        assertThat(firstMethodOf(generatedClass).isPresent(), is(true));
        final Method method = firstMethodOf(generatedClass).get();
        assertThat(method.getParameterCount(), is(1));
        assertThat(method.getParameters()[0].getType(), equalTo(JsonEnvelope.class));
    }

    @Test
    public void shouldGenerateClassIfServiceNameContainsHyphens() throws Exception {

        final SubscriptionWrapper subscriptionWrapper = subscriptionWrapperFactory.createWith("jms:topic:public.event", "cakeshop.actionabc", "context-with-hyphens", "COMMAND_CONTROLLER");

        generator.run(
                subscriptionWrapper,
                configurationWithBasePackage(BASE_PACKAGE, outputFolder, generatorProperties().withServiceComponentOf("COMMAND_CONTROLLER")));

        COMPILER.compiledClassOf(
                outputFolder.getRoot(),
                outputFolder.getRoot(),
                BASE_PACKAGE,
                "RemoteCommandController2PublicEventContextWithHyphensMessagingClient");
    }
}
