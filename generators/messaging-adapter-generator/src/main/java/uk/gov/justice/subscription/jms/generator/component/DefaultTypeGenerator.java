package uk.gov.justice.subscription.jms.generator.component;

import uk.gov.justice.services.generators.commons.config.CommonGeneratorProperties;
import uk.gov.justice.services.generators.subscription.parser.SubscriptionWrapper;
import uk.gov.justice.subscription.domain.subscriptiondescriptor.Subscription;
import uk.gov.justice.subscription.jms.generator.ClassNameFactory;
import uk.gov.justice.subscription.jms.generator.core.MessageListenerCodeGenerator;
import uk.gov.justice.subscription.jms.generator.core.ServiceComponentStrategy;
import uk.gov.justice.subscription.jms.generator.interceptor.JmsLoggerMetadataInterceptorCodeGenerator;

import java.util.stream.Stream;

import com.squareup.javapoet.TypeSpec;

public class DefaultTypeGenerator implements ServiceComponentTypeGenerator {

    private final MessageListenerCodeGenerator messageListenerCodeGenerator;
    private final JmsLoggerMetadataInterceptorCodeGenerator jmsLoggerMetadataInterceptorCodeGenerator;
    private final ClassNameFactory classNameFactory;
    private final ServiceComponentStrategy serviceComponentStrategy;

    public DefaultTypeGenerator(final MessageListenerCodeGenerator messageListenerCodeGenerator,
                                final JmsLoggerMetadataInterceptorCodeGenerator jmsLoggerMetadataInterceptorCodeGenerator,
                                final ClassNameFactory classNameFactory,
                                final ServiceComponentStrategy serviceComponentStrategy) {
        this.messageListenerCodeGenerator = messageListenerCodeGenerator;
        this.jmsLoggerMetadataInterceptorCodeGenerator = jmsLoggerMetadataInterceptorCodeGenerator;
        this.classNameFactory = classNameFactory;
        this.serviceComponentStrategy = serviceComponentStrategy;
    }

    @Override
    public Stream.Builder<TypeSpec> generatedClassesFrom(final Stream.Builder<TypeSpec> streamBuilder,
                                                         final SubscriptionWrapper subscriptionWrapper,
                                                         final Subscription subscription,
                                                         final CommonGeneratorProperties commonGeneratorProperties) {
        return streamBuilder
                .add(jmsLoggerMetadataInterceptorCodeGenerator.generate(
                        subscriptionWrapper,
                        classNameFactory))
                .add(messageListenerCodeGenerator.generate(
                        subscriptionWrapper,
                        subscription,
                        commonGeneratorProperties,
                        classNameFactory,
                        serviceComponentStrategy));
    }
}
