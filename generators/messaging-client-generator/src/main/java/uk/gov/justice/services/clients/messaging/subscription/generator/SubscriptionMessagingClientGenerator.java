package uk.gov.justice.services.clients.messaging.subscription.generator;

import static uk.gov.justice.services.generators.commons.helper.GeneratedClassWriter.writeClass;

import uk.gov.justice.maven.generator.io.files.parser.core.Generator;
import uk.gov.justice.maven.generator.io.files.parser.core.GeneratorConfig;
import uk.gov.justice.services.generators.subscription.parser.SubscriptionWrapper;
import uk.gov.justice.subscription.domain.eventsource.EventSourceDefinition;
import uk.gov.justice.subscription.domain.subscriptiondescriptor.Subscription;
import uk.gov.justice.subscription.domain.subscriptiondescriptor.SubscriptionsDescriptor;

import java.util.List;
import java.util.stream.Stream;

import com.squareup.javapoet.TypeSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubscriptionMessagingClientGenerator implements Generator<SubscriptionWrapper> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionMessagingClientGenerator.class);
    private final RemoteMessagingClientCodeGenerator remoteMessagingClientCodeGenerator;

    public SubscriptionMessagingClientGenerator(final RemoteMessagingClientCodeGenerator remoteMessagingClientCodeGenerator) {
        this.remoteMessagingClientCodeGenerator = remoteMessagingClientCodeGenerator;
    }

    @Override
    public void run(final SubscriptionWrapper subscriptionWrapper, final GeneratorConfig generatorConfig) {

        final String basePackageName = generatorConfig.getBasePackageName();
        final SubscriptionsDescriptor subscriptionsDescriptor = subscriptionWrapper.getSubscriptionsDescriptor();
        final List<Subscription> subscriptions = subscriptionsDescriptor.getSubscriptions();

        subscriptions.stream()
                .flatMap(subscription -> generatedClassesFrom(subscriptionWrapper, subscription, basePackageName))
                .forEach(generatedClass -> writeClass(generatorConfig, basePackageName, generatedClass, LOGGER));
    }

    private Stream<TypeSpec> generatedClassesFrom(final SubscriptionWrapper subscriptionWrapper,
                                                  final Subscription subscription,
                                                  final String basePackageName) {

        final Stream.Builder<TypeSpec> streamBuilder = Stream.builder();
        final SubscriptionsDescriptor subscriptionsDescriptor = subscriptionWrapper.getSubscriptionsDescriptor();
        final String contextName = subscriptionsDescriptor.getService();
        final String componentName = subscriptionsDescriptor.getServiceComponent();

        final EventSourceDefinition eventSourceDefinition = subscriptionWrapper.getEventSourceByName(subscription.getEventSourceName());
        final ClassNameAndMethodNameFactory classNameAndMethodNameFactory = new ClassNameAndMethodNameFactory(
                basePackageName,
                contextName,
                componentName,
                eventSourceDefinition.getLocation().getJmsUri());

        return streamBuilder
                .add(remoteMessagingClientCodeGenerator.classFor(subscription, eventSourceDefinition, componentName, classNameAndMethodNameFactory))
                .build();
    }
}
