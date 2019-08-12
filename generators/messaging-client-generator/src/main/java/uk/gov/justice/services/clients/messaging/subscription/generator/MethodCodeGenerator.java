package uk.gov.justice.services.clients.messaging.subscription.generator;

import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static com.squareup.javapoet.TypeName.VOID;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;

import uk.gov.justice.services.core.annotation.Handles;
import uk.gov.justice.services.generators.subscription.parser.JmsUriToDestinationConverter;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.subscription.domain.eventsource.EventSourceDefinition;
import uk.gov.justice.subscription.domain.subscriptiondescriptor.Event;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;

public class MethodCodeGenerator {

    private final JmsUriToDestinationConverter jmsUriToDestinationConverter = new JmsUriToDestinationConverter();

    public MethodSpec methodFor(final Event event,
                                final EventSourceDefinition eventSourceDefinition,
                                final ClassNameAndMethodNameFactory classNameAndMethodNameFactory) {

        return methodFor(event.getName(), classNameAndMethodNameFactory)
                .addCode(methodBodyOf(eventSourceDefinition))
                .returns(VOID)
                .build();
    }

    private MethodSpec.Builder methodFor(final String name, final ClassNameAndMethodNameFactory classNameAndMethodNameFactory) {

        final String methodName = classNameAndMethodNameFactory.postMethodNameFor(name);

        return methodBuilder(methodName)
                .addModifiers(PUBLIC)
                .addAnnotation(AnnotationSpec.builder(Handles.class)
                        .addMember("value", "$S", name)
                        .build())
                .addParameter(ParameterSpec.builder(JsonEnvelope.class, "envelope")
                        .addModifiers(FINAL)
                        .build())
                .addStatement("$L.trace(LOGGER, () -> String.format(\"Handling remote request: %s\", envelope))",
                        "traceLogger");
    }

    private CodeBlock methodBodyOf(final EventSourceDefinition eventSourceDefinition) {

        final String destination = jmsUriToDestinationConverter.convert(eventSourceDefinition.getLocation().getJmsUri());

        return CodeBlock.builder()
                .addStatement("$L.send($L, $S)", "sender", "envelope", destination)
                .build();
    }
}
