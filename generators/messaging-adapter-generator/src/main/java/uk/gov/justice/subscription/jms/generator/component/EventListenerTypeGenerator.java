package uk.gov.justice.subscription.jms.generator.component;

import uk.gov.justice.services.generators.commons.config.CommonGeneratorProperties;
import uk.gov.justice.services.generators.subscription.parser.SubscriptionWrapper;
import uk.gov.justice.subscription.domain.subscriptiondescriptor.Subscription;
import uk.gov.justice.subscription.jms.generator.ClassNameFactory;
import uk.gov.justice.subscription.jms.generator.framework.EventFilterCodeGenerator;
import uk.gov.justice.subscription.jms.generator.framework.EventFilterInterceptorCodeGenerator;
import uk.gov.justice.subscription.jms.generator.framework.EventInterceptorChainProviderCodeGenerator;
import uk.gov.justice.subscription.jms.generator.framework.EventValidationInterceptorCodeGenerator;
import uk.gov.justice.subscription.jms.generator.interceptor.JmsEventErrorReporterInterceptorCodeGenerator;

import java.util.stream.Stream;

import com.squareup.javapoet.TypeSpec;

public class EventListenerTypeGenerator implements ServiceComponentTypeGenerator {

    private final DefaultTypeGenerator defaultComponentTypeGenerator;
    private final EventFilterCodeGenerator eventFilterCodeGenerator;
    private final EventFilterInterceptorCodeGenerator eventFilterInterceptorCodeGenerator;
    private final EventValidationInterceptorCodeGenerator eventValidationInterceptorCodeGenerator;
    private final EventInterceptorChainProviderCodeGenerator eventInterceptorChainProviderCodeGenerator;
    private final JmsEventErrorReporterInterceptorCodeGenerator jmsEventErrorReporterInterceptorCodeGenerator;
    private final ClassNameFactory classNameFactory;

    public EventListenerTypeGenerator(final DefaultTypeGenerator defaultComponentTypeGenerator,
                                      final EventFilterCodeGenerator eventFilterCodeGenerator,
                                      final EventFilterInterceptorCodeGenerator eventFilterInterceptorCodeGenerator,
                                      final EventValidationInterceptorCodeGenerator eventValidationInterceptorCodeGenerator,
                                      final EventInterceptorChainProviderCodeGenerator eventInterceptorChainProviderCodeGenerator,
                                      final JmsEventErrorReporterInterceptorCodeGenerator jmsEventErrorReporterInterceptorCodeGenerator,
                                      final ClassNameFactory classNameFactory) {
        this.defaultComponentTypeGenerator = defaultComponentTypeGenerator;
        this.eventFilterCodeGenerator = eventFilterCodeGenerator;
        this.eventFilterInterceptorCodeGenerator = eventFilterInterceptorCodeGenerator;
        this.eventValidationInterceptorCodeGenerator = eventValidationInterceptorCodeGenerator;
        this.eventInterceptorChainProviderCodeGenerator = eventInterceptorChainProviderCodeGenerator;
        this.jmsEventErrorReporterInterceptorCodeGenerator = jmsEventErrorReporterInterceptorCodeGenerator;
        this.classNameFactory = classNameFactory;
    }

    @Override
    public Stream.Builder<TypeSpec> generatedClassesFrom(final Stream.Builder<TypeSpec> streamBuilder,
                                                         final SubscriptionWrapper subscriptionWrapper,
                                                         final Subscription subscription,
                                                         final CommonGeneratorProperties commonGeneratorProperties) {

        streamBuilder
                .add(eventFilterCodeGenerator.generate(subscription, classNameFactory))
                .add(eventFilterInterceptorCodeGenerator.generate(classNameFactory))
                .add(eventValidationInterceptorCodeGenerator.generate(classNameFactory))
                .add(eventInterceptorChainProviderCodeGenerator.generate(
                        commonGeneratorProperties.getServiceComponent(),
                        classNameFactory))
                .add(jmsEventErrorReporterInterceptorCodeGenerator.generate(
                        subscriptionWrapper,
                        classNameFactory));

        return defaultComponentTypeGenerator.generatedClassesFrom(
                streamBuilder,
                subscriptionWrapper,
                subscription,
                commonGeneratorProperties
        );
    }
}
