package uk.gov.justice.subscription.jms.generator.component;

import static uk.gov.justice.services.core.annotation.Component.COMMAND_HANDLER;
import static uk.gov.justice.services.core.annotation.Component.EVENT_INDEXER;
import static uk.gov.justice.services.core.annotation.Component.EVENT_LISTENER;

import uk.gov.justice.services.generators.subscription.parser.SubscriptionWrapper;
import uk.gov.justice.subscription.domain.eventsource.EventSourceDefinition;
import uk.gov.justice.subscription.domain.subscriptiondescriptor.Event;
import uk.gov.justice.subscription.domain.subscriptiondescriptor.Subscription;
import uk.gov.justice.subscription.domain.subscriptiondescriptor.SubscriptionsDescriptor;
import uk.gov.justice.subscription.jms.generator.ClassNameFactory;
import uk.gov.justice.subscription.jms.generator.JmsEndpointGenerationObjects;
import uk.gov.justice.subscription.jms.generator.core.DefaultServiceComponentStrategy;

import java.util.List;

public class ServiceComponentGeneratorTypesFactory {

    private final JmsEndpointGenerationObjects jmsEndpointGenerationObjects;

    public ServiceComponentGeneratorTypesFactory(final JmsEndpointGenerationObjects jmsEndpointGenerationObjects) {
        this.jmsEndpointGenerationObjects = jmsEndpointGenerationObjects;
    }

    public ServiceComponentTypeGenerator componentTypeGeneratorFrom(final SubscriptionWrapper subscriptionWrapper,
                                                                    final Subscription subscription,
                                                                    final String basePackageName) {

        final SubscriptionsDescriptor subscriptionsDescriptor = subscriptionWrapper.getSubscriptionsDescriptor();
        final String contextName = subscriptionsDescriptor.getService();
        final String componentName = subscriptionsDescriptor.getServiceComponent();
        final EventSourceDefinition eventSourceDefinition = subscriptionWrapper.getEventSourceByName(subscription.getEventSourceName());

        final ClassNameFactory classNameFactory = new ClassNameFactory(
                basePackageName,
                contextName,
                componentName,
                eventSourceDefinition.getLocation().getJmsUri());


        if (shouldGenerateEventFilter(subscription.getEvents(), componentName)) {
            return createEventComponentTypeGenerator(classNameFactory, subscription);
        }

        if (COMMAND_HANDLER.equals(componentName)) {
            return createCommandHandlerComponentTypeGenerator(classNameFactory, subscription);
        }

        return new DefaultTypeGenerator(
                jmsEndpointGenerationObjects.messageListenerCodeGenerator(),
                jmsEndpointGenerationObjects.jmsLoggerMetadataInterceptorCodeGenerator(),
                classNameFactory,
                new DefaultServiceComponentStrategy(classNameFactory, subscription));
    }

    private ServiceComponentTypeGenerator createEventComponentTypeGenerator(final ClassNameFactory classNameFactory, final Subscription subscription) {
        final DefaultTypeGenerator defaultComponentTypeGenerator = new DefaultTypeGenerator(
                jmsEndpointGenerationObjects.messageListenerCodeGenerator(),
                jmsEndpointGenerationObjects.jmsLoggerMetadataInterceptorCodeGenerator(),
                classNameFactory,
                jmsEndpointGenerationObjects.eventComponentStrategy(classNameFactory, subscription));

        return new EventListenerTypeGenerator(
                defaultComponentTypeGenerator,
                jmsEndpointGenerationObjects.eventFilterCodeGenerator(),
                jmsEndpointGenerationObjects.eventFilterInterceptorCodeGenerator(),
                jmsEndpointGenerationObjects.eventValidationInterceptorCodeGenerator(),
                jmsEndpointGenerationObjects.eventInterceptorChainProviderCodeGenerator(),
                jmsEndpointGenerationObjects.jmsEventErrorReporterInterceptorCodeGenerator(),
                classNameFactory);
    }

    private ServiceComponentTypeGenerator createCommandHandlerComponentTypeGenerator(final ClassNameFactory classNameFactory, final Subscription subscription) {

        final DefaultTypeGenerator defaultComponentTypeGenerator = new DefaultTypeGenerator(
                jmsEndpointGenerationObjects.messageListenerCodeGenerator(),
                jmsEndpointGenerationObjects.jmsLoggerMetadataInterceptorCodeGenerator(),
                classNameFactory,
                new DefaultServiceComponentStrategy(classNameFactory, subscription));

        return new CommandHandlerTypeGenerator(
                defaultComponentTypeGenerator,
                jmsEndpointGenerationObjects.jmsCommandHandlerDestinationNameProviderCodeGenerator(),
                classNameFactory);
    }

    private boolean shouldGenerateEventFilter(final List<Event> events, final String component) {
        return (component.contains(EVENT_LISTENER) || component.contains(EVENT_INDEXER)) && !events.isEmpty();
    }
}
