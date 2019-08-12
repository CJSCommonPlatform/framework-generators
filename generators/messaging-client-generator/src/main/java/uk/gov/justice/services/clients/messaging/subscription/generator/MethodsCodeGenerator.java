package uk.gov.justice.services.clients.messaging.subscription.generator;

import static java.util.stream.Collectors.toList;

import uk.gov.justice.subscription.domain.eventsource.EventSourceDefinition;
import uk.gov.justice.subscription.domain.subscriptiondescriptor.Subscription;

import java.util.List;

import com.squareup.javapoet.MethodSpec;

public class MethodsCodeGenerator {

    private final MethodCodeGenerator methodCodeGenerator;

    public MethodsCodeGenerator(final MethodCodeGenerator methodCodeGenerator) {
        this.methodCodeGenerator = methodCodeGenerator;
    }

    public List<MethodSpec> methodsFor(final Subscription subscription,
                                       final EventSourceDefinition eventSourceDefinition,
                                       final ClassNameAndMethodNameFactory classNameAndMethodNameFactory) {
        return subscription.getEvents().stream()
                .map(event -> methodCodeGenerator.methodFor(
                        event,
                        eventSourceDefinition,
                        classNameAndMethodNameFactory)
                ).collect(toList());
    }

}
