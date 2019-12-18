package uk.gov.justice.subscription.jms.generator.component;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.core.annotation.Component.COMMAND_HANDLER;
import static uk.gov.justice.services.core.annotation.Component.EVENT_INDEXER;
import static uk.gov.justice.services.core.annotation.Component.EVENT_LISTENER;
import static uk.gov.justice.services.core.annotation.Component.EVENT_PROCESSOR;
import static uk.gov.justice.services.test.utils.core.reflection.ReflectionUtil.getValueOfField;
import static uk.gov.justice.subscription.domain.builders.EventBuilder.event;
import static uk.gov.justice.subscription.domain.builders.EventSourceDefinitionBuilder.eventSourceDefinition;
import static uk.gov.justice.subscription.domain.builders.LocationBuilder.location;
import static uk.gov.justice.subscription.domain.builders.SubscriptionBuilder.subscription;
import static uk.gov.justice.subscription.domain.builders.SubscriptionsDescriptorBuilder.subscriptionsDescriptor;

import uk.gov.justice.services.generators.subscription.parser.SubscriptionWrapper;
import uk.gov.justice.subscription.domain.eventsource.EventSourceDefinition;
import uk.gov.justice.subscription.domain.subscriptiondescriptor.Event;
import uk.gov.justice.subscription.domain.subscriptiondescriptor.Subscription;
import uk.gov.justice.subscription.domain.subscriptiondescriptor.SubscriptionsDescriptor;
import uk.gov.justice.subscription.jms.generator.ClassNameFactory;
import uk.gov.justice.subscription.jms.generator.JmsEndpointGenerationObjects;
import uk.gov.justice.subscription.jms.generator.core.MessageListenerCodeGenerator;
import uk.gov.justice.subscription.jms.generator.framework.EventFilterCodeGenerator;
import uk.gov.justice.subscription.jms.generator.framework.EventFilterInterceptorCodeGenerator;
import uk.gov.justice.subscription.jms.generator.framework.EventInterceptorChainProviderCodeGenerator;
import uk.gov.justice.subscription.jms.generator.framework.EventValidationInterceptorCodeGenerator;
import uk.gov.justice.subscription.jms.generator.interceptor.JmsEventErrorReporterInterceptorCodeGenerator;
import uk.gov.justice.subscription.jms.generator.interceptor.JmsLoggerMetadataInterceptorCodeGenerator;
import uk.gov.justice.subscription.jms.provider.JmsCommandHandlerDestinationNameProviderCodeGenerator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ServiceComponentGeneratorTypesFactoryTest {

    @Mock
    private JmsEndpointGenerationObjects jmsEndpointGenerationObjects;

    @InjectMocks
    private ServiceComponentGeneratorTypesFactory serviceComponentGeneratorTypesFactory;

    private Subscription subscription;

    @Test
    public void shouldCreateDefaultComponentGenerator() {

        final SubscriptionWrapper subscriptionWrapper = setUpMessageSubscription("jms:topic:example", "my-context.events.something-happened", "example", EVENT_PROCESSOR);
        final MessageListenerCodeGenerator messageListenerCodeGenerator = mock(MessageListenerCodeGenerator.class);
        final JmsLoggerMetadataInterceptorCodeGenerator jmsLoggerMetadataInterceptorCodeGenerator = mock(JmsLoggerMetadataInterceptorCodeGenerator.class);

        when(jmsEndpointGenerationObjects.messageListenerCodeGenerator()).thenReturn(messageListenerCodeGenerator);
        when(jmsEndpointGenerationObjects.jmsLoggerMetadataInterceptorCodeGenerator()).thenReturn(jmsLoggerMetadataInterceptorCodeGenerator);

        final ServiceComponentTypeGenerator serviceComponentTypeGenerator = serviceComponentGeneratorTypesFactory.componentTypeGeneratorFrom(subscriptionWrapper, subscription, "base.package");

        assertThat(serviceComponentTypeGenerator, instanceOf(DefaultTypeGenerator.class));
        assertThat(getValueOfField(serviceComponentTypeGenerator, "messageListenerCodeGenerator", MessageListenerCodeGenerator.class), is(messageListenerCodeGenerator));
        assertThat(getValueOfField(serviceComponentTypeGenerator, "jmsLoggerMetadataInterceptorCodeGenerator", JmsLoggerMetadataInterceptorCodeGenerator.class), is(jmsLoggerMetadataInterceptorCodeGenerator));
        assertThat(getValueOfField(serviceComponentTypeGenerator, "jmsLoggerMetadataInterceptorCodeGenerator", JmsLoggerMetadataInterceptorCodeGenerator.class), is(jmsLoggerMetadataInterceptorCodeGenerator));
        assertThat(getValueOfField(serviceComponentTypeGenerator, "classNameFactory", ClassNameFactory.class), notNullValue());
    }

    @Test
    public void shouldCreateEventComponentTypeGeneratorForEventListener() {

        final SubscriptionWrapper subscriptionWrapper = setUpMessageSubscription("jms:topic:example", "my-context.events.something-happened", "example", EVENT_LISTENER);
        final EventFilterCodeGenerator eventFilterCodeGenerator = mock(EventFilterCodeGenerator.class);
        final EventFilterInterceptorCodeGenerator eventFilterInterceptorCodeGenerator = mock(EventFilterInterceptorCodeGenerator.class);
        final EventValidationInterceptorCodeGenerator eventValidationInterceptorCodeGenerator = mock(EventValidationInterceptorCodeGenerator.class);
        final EventInterceptorChainProviderCodeGenerator eventInterceptorChainProviderCodeGenerator = mock(EventInterceptorChainProviderCodeGenerator.class);
        final JmsEventErrorReporterInterceptorCodeGenerator jmsEventErrorReporterInterceptorCodeGenerator = mock(JmsEventErrorReporterInterceptorCodeGenerator.class);

        when(jmsEndpointGenerationObjects.eventFilterCodeGenerator()).thenReturn(eventFilterCodeGenerator);
        when(jmsEndpointGenerationObjects.eventFilterInterceptorCodeGenerator()).thenReturn(eventFilterInterceptorCodeGenerator);
        when(jmsEndpointGenerationObjects.eventValidationInterceptorCodeGenerator()).thenReturn(eventValidationInterceptorCodeGenerator);
        when(jmsEndpointGenerationObjects.eventInterceptorChainProviderCodeGenerator()).thenReturn(eventInterceptorChainProviderCodeGenerator);
        when(jmsEndpointGenerationObjects.jmsEventErrorReporterInterceptorCodeGenerator()).thenReturn(jmsEventErrorReporterInterceptorCodeGenerator);

        final ServiceComponentTypeGenerator serviceComponentTypeGenerator = serviceComponentGeneratorTypesFactory.componentTypeGeneratorFrom(subscriptionWrapper, subscription, "base.package");

        assertThat(serviceComponentTypeGenerator, instanceOf(EventListenerTypeGenerator.class));
        assertThat(getValueOfField(serviceComponentTypeGenerator, "defaultComponentTypeGenerator", DefaultTypeGenerator.class), instanceOf(DefaultTypeGenerator.class));
        assertThat(getValueOfField(serviceComponentTypeGenerator, "eventFilterCodeGenerator", EventFilterCodeGenerator.class), is(eventFilterCodeGenerator));
        assertThat(getValueOfField(serviceComponentTypeGenerator, "eventFilterInterceptorCodeGenerator", EventFilterInterceptorCodeGenerator.class), is(eventFilterInterceptorCodeGenerator));
        assertThat(getValueOfField(serviceComponentTypeGenerator, "eventValidationInterceptorCodeGenerator", EventValidationInterceptorCodeGenerator.class), is(eventValidationInterceptorCodeGenerator));
        assertThat(getValueOfField(serviceComponentTypeGenerator, "eventInterceptorChainProviderCodeGenerator", EventInterceptorChainProviderCodeGenerator.class), is(eventInterceptorChainProviderCodeGenerator));
        assertThat(getValueOfField(serviceComponentTypeGenerator, "jmsEventErrorReporterInterceptorCodeGenerator", JmsEventErrorReporterInterceptorCodeGenerator.class), is(jmsEventErrorReporterInterceptorCodeGenerator));
        assertThat(getValueOfField(serviceComponentTypeGenerator, "classNameFactory", ClassNameFactory.class), notNullValue());
    }

    @Test
    public void shouldCreateEventComponentTypeGeneratorForEventIndex() {

        final SubscriptionWrapper subscriptionWrapper = setUpMessageSubscription("jms:topic:example", "my-context.events.something-happened", "example", EVENT_INDEXER);
        final EventFilterCodeGenerator eventFilterCodeGenerator = mock(EventFilterCodeGenerator.class);
        final EventFilterInterceptorCodeGenerator eventFilterInterceptorCodeGenerator = mock(EventFilterInterceptorCodeGenerator.class);
        final EventValidationInterceptorCodeGenerator eventValidationInterceptorCodeGenerator = mock(EventValidationInterceptorCodeGenerator.class);
        final EventInterceptorChainProviderCodeGenerator eventInterceptorChainProviderCodeGenerator = mock(EventInterceptorChainProviderCodeGenerator.class);
        final JmsEventErrorReporterInterceptorCodeGenerator jmsEventErrorReporterInterceptorCodeGenerator = mock(JmsEventErrorReporterInterceptorCodeGenerator.class);

        when(jmsEndpointGenerationObjects.eventFilterCodeGenerator()).thenReturn(eventFilterCodeGenerator);
        when(jmsEndpointGenerationObjects.eventFilterInterceptorCodeGenerator()).thenReturn(eventFilterInterceptorCodeGenerator);
        when(jmsEndpointGenerationObjects.eventValidationInterceptorCodeGenerator()).thenReturn(eventValidationInterceptorCodeGenerator);
        when(jmsEndpointGenerationObjects.eventInterceptorChainProviderCodeGenerator()).thenReturn(eventInterceptorChainProviderCodeGenerator);
        when(jmsEndpointGenerationObjects.jmsEventErrorReporterInterceptorCodeGenerator()).thenReturn(jmsEventErrorReporterInterceptorCodeGenerator);

        final ServiceComponentTypeGenerator serviceComponentTypeGenerator = serviceComponentGeneratorTypesFactory.componentTypeGeneratorFrom(subscriptionWrapper, subscription, "base.package");

        assertThat(serviceComponentTypeGenerator, instanceOf(EventListenerTypeGenerator.class));
        assertThat(getValueOfField(serviceComponentTypeGenerator, "defaultComponentTypeGenerator", DefaultTypeGenerator.class), instanceOf(DefaultTypeGenerator.class));
        assertThat(getValueOfField(serviceComponentTypeGenerator, "eventFilterCodeGenerator", EventFilterCodeGenerator.class), is(eventFilterCodeGenerator));
        assertThat(getValueOfField(serviceComponentTypeGenerator, "eventFilterInterceptorCodeGenerator", EventFilterInterceptorCodeGenerator.class), is(eventFilterInterceptorCodeGenerator));
        assertThat(getValueOfField(serviceComponentTypeGenerator, "eventValidationInterceptorCodeGenerator", EventValidationInterceptorCodeGenerator.class), is(eventValidationInterceptorCodeGenerator));
        assertThat(getValueOfField(serviceComponentTypeGenerator, "eventInterceptorChainProviderCodeGenerator", EventInterceptorChainProviderCodeGenerator.class), is(eventInterceptorChainProviderCodeGenerator));
        assertThat(getValueOfField(serviceComponentTypeGenerator, "jmsEventErrorReporterInterceptorCodeGenerator", JmsEventErrorReporterInterceptorCodeGenerator.class), is(jmsEventErrorReporterInterceptorCodeGenerator));
        assertThat(getValueOfField(serviceComponentTypeGenerator, "classNameFactory", ClassNameFactory.class), notNullValue());
    }

    @Test
    public void shouldCreateCommandHandlerComponentTypeGeneratorForCommandHandler() {

        final SubscriptionWrapper subscriptionWrapper = setUpMessageSubscription("jms:topic:example", "my-context.events.something-happened", "example", COMMAND_HANDLER);
        final JmsCommandHandlerDestinationNameProviderCodeGenerator jmsCommandHandlerDestinationNameProviderCodeGenerator = mock(JmsCommandHandlerDestinationNameProviderCodeGenerator.class);

        when(jmsEndpointGenerationObjects.jmsCommandHandlerDestinationNameProviderCodeGenerator()).thenReturn(jmsCommandHandlerDestinationNameProviderCodeGenerator);

        final ServiceComponentTypeGenerator serviceComponentTypeGenerator = serviceComponentGeneratorTypesFactory.componentTypeGeneratorFrom(subscriptionWrapper, subscription, "base.package");

        assertThat(serviceComponentTypeGenerator, instanceOf(CommandHandlerTypeGenerator.class));
        assertThat(getValueOfField(serviceComponentTypeGenerator, "defaultComponentTypeGenerator", DefaultTypeGenerator.class), instanceOf(DefaultTypeGenerator.class));
        assertThat(getValueOfField(serviceComponentTypeGenerator, "jmsCommandHandlerDestinationNameProviderCodeGenerator", JmsCommandHandlerDestinationNameProviderCodeGenerator.class), is(jmsCommandHandlerDestinationNameProviderCodeGenerator));
        assertThat(getValueOfField(serviceComponentTypeGenerator, "classNameFactory", ClassNameFactory.class), notNullValue());
    }

    private SubscriptionWrapper setUpMessageSubscription(final String jmsUri, final String eventName, final String serviceName, final String componentName) {
        final Event event = event()
                .withName(eventName)
                .withSchemaUri("http://justice.gov.uk/json/schemas/domains/example/" + eventName + ".json")
                .build();

        final EventSourceDefinition eventSourceDefinition = eventSourceDefinition()
                .withName("eventSource")
                .withLocation(location()
                        .withJmsUri(jmsUri)
                        .withRestUri("http://localhost:8080/example/event-source-api/rest")
                        .build())
                .build();

        subscription = subscription()
                .withName("subscription")
                .withEvent(event)
                .withEventSourceName("eventSource")
                .build();

        final SubscriptionsDescriptor subscriptionsDescriptor = subscriptionsDescriptor()
                .withSpecVersion("1.0.0")
                .withService(serviceName)
                .withServiceComponent(componentName)
                .withSubscription(subscription)
                .build();

        return new SubscriptionWrapper(subscriptionsDescriptor, singletonList(eventSourceDefinition));
    }
}