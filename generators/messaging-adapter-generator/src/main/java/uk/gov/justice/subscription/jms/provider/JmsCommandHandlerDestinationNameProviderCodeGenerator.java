package uk.gov.justice.subscription.jms.provider;

import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static com.squareup.javapoet.TypeSpec.classBuilder;
import static javax.lang.model.element.Modifier.PUBLIC;
import static uk.gov.justice.subscription.jms.core.ClassNameFactory.JMS_HANDLER_DESTINATION_NAME_PROVIDER;

import uk.gov.justice.services.generators.subscription.parser.JmsUriToDestinationConverter;
import uk.gov.justice.services.generators.subscription.parser.SubscriptionWrapper;
import uk.gov.justice.services.messaging.jms.JmsCommandHandlerDestinationNameProvider;
import uk.gov.justice.subscription.domain.eventsource.EventSourceDefinition;
import uk.gov.justice.subscription.domain.subscriptiondescriptor.Subscription;
import uk.gov.justice.subscription.jms.core.ClassNameFactory;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

/**
 * Should generate a JmsCommandHandlerDestinationNameProvider for a Command Handler component.
 * Something like this:
 *
 * <pre>
 *  {@code
 *
 *      public class MyCommandHandlerJmsHandlerDestinationNameProvider implements JmsCommandHandlerDestinationNameProvider {
 *
 *          @Override
 *          public String destinationName() {
 *              return "my.handler.command";
 *          }
 *      }
 * }
 * </pre>
 */
public class JmsCommandHandlerDestinationNameProviderCodeGenerator {

    private final JmsUriToDestinationConverter jmsUriToDestinationConverter = new JmsUriToDestinationConverter();

    public TypeSpec generate(final SubscriptionWrapper subscriptionWrapper,
                             final Subscription subscription,
                             final ClassNameFactory classNameFactory) {

        final ClassName className = classNameFactory.classNameFor(JMS_HANDLER_DESTINATION_NAME_PROVIDER);

        return classBuilder(className)
                .addModifiers(PUBLIC)
                .addSuperinterface(JmsCommandHandlerDestinationNameProvider.class)
                .addMethod(createJmsHandlerDestinationNameMethod(subscriptionWrapper, subscription))
                .build();

    }

    private MethodSpec createJmsHandlerDestinationNameMethod(final SubscriptionWrapper subscriptionWrapper,
                                                             final Subscription subscription) {

        final EventSourceDefinition eventSourceDefinition = subscriptionWrapper.getEventSourceByName(subscription.getEventSourceName());
        final String destination = jmsUriToDestinationConverter.convert(eventSourceDefinition.getLocation().getJmsUri());

        return methodBuilder("destinationName")
                .addModifiers(PUBLIC)
                .addAnnotation(Override.class)
                .addStatement("return \"$L\"", destination)
                .returns(String.class)
                .build();
    }
}
