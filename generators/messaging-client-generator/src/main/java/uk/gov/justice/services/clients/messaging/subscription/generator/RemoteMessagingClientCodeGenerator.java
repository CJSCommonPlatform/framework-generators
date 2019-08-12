package uk.gov.justice.services.clients.messaging.subscription.generator;

import static java.lang.String.format;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;

import uk.gov.justice.services.core.annotation.FrameworkComponent;
import uk.gov.justice.services.core.annotation.Remote;
import uk.gov.justice.services.messaging.jms.JmsEnvelopeSender;
import uk.gov.justice.services.messaging.logging.TraceLogger;
import uk.gov.justice.subscription.domain.eventsource.EventSourceDefinition;
import uk.gov.justice.subscription.domain.subscriptiondescriptor.Subscription;

import javax.inject.Inject;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteMessagingClientCodeGenerator {

    private final MethodsCodeGenerator methodsCodeGenerator;

    public RemoteMessagingClientCodeGenerator(final MethodsCodeGenerator methodsCodeGenerator) {
        this.methodsCodeGenerator = methodsCodeGenerator;
    }

    public TypeSpec classFor(final Subscription subscription,
                             final EventSourceDefinition eventSourceDefinition,
                             final String componentName,
                             final ClassNameAndMethodNameFactory classNameAndMethodNameFactory) {

        return classSpecOf(componentName, classNameAndMethodNameFactory)
                .addField(FieldSpec.builder(JmsEnvelopeSender.class, "sender")
                        .addAnnotation(Inject.class)
                        .build())
                .addField(FieldSpec.builder(TraceLogger.class, "traceLogger")
                        .addAnnotation(Inject.class)
                        .build())
                .addMethods(methodsCodeGenerator.methodsFor(subscription, eventSourceDefinition, classNameAndMethodNameFactory))
                .build();
    }

    private TypeSpec.Builder classSpecOf(final String componentName,
                                         final ClassNameAndMethodNameFactory classNameAndMethodNameFactory) {

        final ClassName className = classNameAndMethodNameFactory.classNameFor(ClassNameAndMethodNameFactory.REMOTE_MESSAGING_CLIENT);

        return TypeSpec.classBuilder(className)
                .addModifiers(PUBLIC, FINAL)
                .addAnnotation(AnnotationSpec.builder(Remote.class).build())
                .addAnnotation(AnnotationSpec.builder(FrameworkComponent.class)
                        .addMember("value", "$S", componentName)
                        .build())
                .addField(loggerConstantField(className));
    }

    private FieldSpec loggerConstantField(final ClassName className) {

        final ClassName classLoggerFactory = ClassName.get(LoggerFactory.class);

        return FieldSpec.builder(Logger.class, "LOGGER")
                .addModifiers(PRIVATE, javax.lang.model.element.Modifier.STATIC, FINAL)
                .initializer(
                        CodeBlock.builder()
                                .add(format("$L.getLogger(%s.class)", className), classLoggerFactory).build()
                )
                .build();
    }
}