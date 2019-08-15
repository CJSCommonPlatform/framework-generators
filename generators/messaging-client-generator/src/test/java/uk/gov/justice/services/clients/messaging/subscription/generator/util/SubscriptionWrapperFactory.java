package uk.gov.justice.services.clients.messaging.subscription.generator.util;

import static java.util.Collections.singletonList;
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

public class SubscriptionWrapperFactory {

    public SubscriptionWrapper createWith(final String jmsUri, final String eventName, final String serviceName, final String componentName) {
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

        final Subscription subscription = subscription()
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
