package uk.gov.justice.subscription.jms.interceptor;

import static com.squareup.javapoet.TypeSpec.classBuilder;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;
import static uk.gov.justice.subscription.jms.core.ClassNameFactory.JMS_EVENT_ERROR_REPORTER_INTERCEPTOR;

import uk.gov.justice.services.adapter.messaging.JmsMessageProcessErrorReporter;
import uk.gov.justice.services.generators.subscription.parser.SubscriptionWrapper;
import uk.gov.justice.subscription.domain.subscriptiondescriptor.SubscriptionsDescriptor;
import uk.gov.justice.subscription.jms.core.ClassNameFactory;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

public class JmsEventErrorReporterInterceptorCodeGenerator {

    private static final String JMS_MESSAGE_PROCESS_ERROR_REPORTER = "jmsMessageProcessErrorReporter";

    /**
     * Generate the a JmsMessageProcessErrorReporter class implementation.
     *
     * @param subscriptionWrapper the subscription descriptor Wrapper
     * @param classNameFactory    creates the class name for this generated class
     * @return the {@link TypeSpec} that defines the class
     */
    public TypeSpec generate(final SubscriptionWrapper subscriptionWrapper,
                             final ClassNameFactory classNameFactory) {

        final SubscriptionsDescriptor subscriptionsDescriptor = subscriptionWrapper.getSubscriptionsDescriptor();
        final String serviceComponent = subscriptionsDescriptor.getServiceComponent().toUpperCase();

        final ClassName className = classNameFactory.classNameFor(JMS_EVENT_ERROR_REPORTER_INTERCEPTOR);

        return classBuilder(className)
                .addModifiers(PUBLIC)
                .addField(FieldSpec.builder(ClassName.get(JmsMessageProcessErrorReporter.class), JMS_MESSAGE_PROCESS_ERROR_REPORTER)
                        .addAnnotation(Inject.class)
                        .build())
                .addMethod(generateCatchAndReportErrorMethod(serviceComponent))
                .build();
    }

    /**
     * Generate the catchAndReportError method.
     *
     * @return the {@link MethodSpec} that represents the catchAndReportError method
     */
    private MethodSpec generateCatchAndReportErrorMethod(final String serviceComponent) {

        final String invocationContextParameter = "invocationContext";

        return MethodSpec.methodBuilder("catchAndReportError")
                .addModifiers(PUBLIC)
                .addAnnotation(AroundInvoke.class)
                .addParameter(ParameterSpec
                        .builder(InvocationContext.class, invocationContextParameter, FINAL)
                        .build())
                .addException(Exception.class)
                .addCode(CodeBlock.builder()
                        .addStatement("return $L.catchAndReportError($L, $S)",
                                JMS_MESSAGE_PROCESS_ERROR_REPORTER,
                                invocationContextParameter,
                                serviceComponent
                        )
                        .build())
                .returns(Object.class)
                .build();
    }
}
