package uk.gov.justice.subscription.jms.generator.component;

import uk.gov.justice.services.generators.commons.config.CommonGeneratorProperties;
import uk.gov.justice.services.generators.subscription.parser.SubscriptionWrapper;
import uk.gov.justice.subscription.domain.subscriptiondescriptor.Subscription;
import uk.gov.justice.subscription.jms.generator.ClassNameFactory;
import uk.gov.justice.subscription.jms.provider.JmsCommandHandlerDestinationNameProviderCodeGenerator;

import java.util.stream.Stream;

import com.squareup.javapoet.TypeSpec;

public class CommandHandlerTypeGenerator implements ServiceComponentTypeGenerator {

    private final DefaultTypeGenerator defaultComponentTypeGenerator;
    private final JmsCommandHandlerDestinationNameProviderCodeGenerator jmsCommandHandlerDestinationNameProviderCodeGenerator;
    private final ClassNameFactory classNameFactory;

    public CommandHandlerTypeGenerator(final DefaultTypeGenerator defaultComponentTypeGenerator,
                                       final JmsCommandHandlerDestinationNameProviderCodeGenerator jmsCommandHandlerDestinationNameProviderCodeGenerator,
                                       final ClassNameFactory classNameFactory) {
        this.defaultComponentTypeGenerator = defaultComponentTypeGenerator;
        this.jmsCommandHandlerDestinationNameProviderCodeGenerator = jmsCommandHandlerDestinationNameProviderCodeGenerator;
        this.classNameFactory = classNameFactory;
    }

    @Override
    public Stream.Builder<TypeSpec> generatedClassesFrom(final Stream.Builder<TypeSpec> streamBuilder,
                                                         final SubscriptionWrapper subscriptionWrapper,
                                                         final Subscription subscription,
                                                         final CommonGeneratorProperties commonGeneratorProperties) {

        streamBuilder.add(jmsCommandHandlerDestinationNameProviderCodeGenerator.generate(subscriptionWrapper, subscription, classNameFactory));

        return defaultComponentTypeGenerator.generatedClassesFrom(
                streamBuilder,
                subscriptionWrapper,
                subscription,
                commonGeneratorProperties
        );
    }
}
