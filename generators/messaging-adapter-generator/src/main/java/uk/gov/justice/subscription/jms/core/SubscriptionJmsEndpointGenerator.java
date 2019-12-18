package uk.gov.justice.subscription.jms.core;

import static java.util.stream.Collectors.toList;
import static uk.gov.justice.services.generators.commons.helper.GeneratedClassWriter.writeClass;

import uk.gov.justice.maven.generator.io.files.parser.core.Generator;
import uk.gov.justice.maven.generator.io.files.parser.core.GeneratorConfig;
import uk.gov.justice.services.generators.commons.config.CommonGeneratorProperties;
import uk.gov.justice.services.generators.commons.mapping.SubscriptionMediaTypeToSchemaIdGenerator;
import uk.gov.justice.services.generators.subscription.parser.SubscriptionWrapper;
import uk.gov.justice.subscription.domain.subscriptiondescriptor.Event;
import uk.gov.justice.subscription.domain.subscriptiondescriptor.Subscription;
import uk.gov.justice.subscription.domain.subscriptiondescriptor.SubscriptionsDescriptor;
import uk.gov.justice.subscription.jms.generator.component.ServiceComponentGeneratorTypesFactory;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import com.squareup.javapoet.TypeSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates JMS endpoint classes out of subscriptions
 */
public class SubscriptionJmsEndpointGenerator implements Generator<SubscriptionWrapper> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionJmsEndpointGenerator.class);

    private final SubscriptionMediaTypeToSchemaIdGenerator subscriptionMediaTypeToSchemaIdGenerator;
    private final ServiceComponentGeneratorTypesFactory serviceComponentGeneratorTypesFactory;

    public SubscriptionJmsEndpointGenerator(final SubscriptionMediaTypeToSchemaIdGenerator subscriptionMediaTypeToSchemaIdGenerator,
                                            final ServiceComponentGeneratorTypesFactory serviceComponentGeneratorTypesFactory) {
        this.subscriptionMediaTypeToSchemaIdGenerator = subscriptionMediaTypeToSchemaIdGenerator;
        this.serviceComponentGeneratorTypesFactory = serviceComponentGeneratorTypesFactory;
    }

    /**
     * Generates JMS endpoint classes from a SubscriptionDescriptorDef document.
     *
     * @param subscriptionWrapper the subscriptionWrapper document
     * @param configuration       contains package of generated sources, as well as source and
     *                            destination folders
     */
    @Override
    public void run(final SubscriptionWrapper subscriptionWrapper, final GeneratorConfig configuration) {

        final CommonGeneratorProperties commonGeneratorProperties = (CommonGeneratorProperties) configuration.getGeneratorProperties();
        final String basePackageName = configuration.getBasePackageName();

        final SubscriptionsDescriptor subscriptionsDescriptor = subscriptionWrapper.getSubscriptionsDescriptor();
        final List<Subscription> subscriptions = subscriptionsDescriptor.getSubscriptions();
        subscriptions.stream()
                .flatMap(subscription -> generatedClassesFrom(subscriptionWrapper, subscription, commonGeneratorProperties, basePackageName))
                .forEach(generatedClass ->
                        writeClass(configuration, basePackageName, generatedClass, LOGGER)
                );

        final List<Event> allEvents = subscriptions.stream()
                .map(Subscription::getEvents)
                .flatMap(Collection::stream)
                .collect(toList());

        final String contextName = subscriptionsDescriptor.getService();
        final String componentName = subscriptionsDescriptor.getServiceComponent();

        subscriptionMediaTypeToSchemaIdGenerator.generateMediaTypeToSchemaIdMapper(
                contextName,
                componentName,
                allEvents,
                configuration);
    }

    private Stream<TypeSpec> generatedClassesFrom(final SubscriptionWrapper subscriptionWrapper,
                                                  final Subscription subscription,
                                                  final CommonGeneratorProperties commonGeneratorProperties,
                                                  final String basePackageName) {

        return serviceComponentGeneratorTypesFactory
                .componentTypeGeneratorFrom(subscriptionWrapper, subscription, basePackageName)
                .generatedClassesFrom(Stream.builder(), subscriptionWrapper, subscription, commonGeneratorProperties)
                .build();
    }
}
