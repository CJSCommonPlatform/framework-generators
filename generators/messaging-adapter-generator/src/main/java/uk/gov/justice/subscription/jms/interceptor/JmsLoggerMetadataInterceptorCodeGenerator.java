package uk.gov.justice.subscription.jms.interceptor;

import static com.squareup.javapoet.TypeSpec.classBuilder;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;
import static uk.gov.justice.raml.jms.core.ClassNameFactory.JMS_LOGGER_METADATA_INTERCEPTOR;

import uk.gov.justice.services.adapter.messaging.JmsLoggerMetadataAdder;
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

public class JmsLoggerMetadataInterceptorCodeGenerator {

    private static final String JMS_LOGGER_METADATA_ADDER_FIELD = "jmsLoggerMetadataAdder";

    /**
     * Generate the a JmsLoggerMetadataInterceptor class implementation.
     *
     * @param subscriptionWrapper the subscription descriptor Wrapper
     * @param classNameFactory    creates the class name for this generated class
     * @return the {@link TypeSpec} that defines the class
     */
    public TypeSpec generate(final SubscriptionWrapper subscriptionWrapper,
                             final ClassNameFactory classNameFactory) {

        final SubscriptionsDescriptor subscriptionsDescriptor = subscriptionWrapper.getSubscriptionsDescriptor();
        final String serviceComponent = subscriptionsDescriptor.getServiceComponent().toUpperCase();

        final ClassName className = classNameFactory.classNameFor(JMS_LOGGER_METADATA_INTERCEPTOR);

        return classBuilder(className)
                .addModifiers(PUBLIC)
                .addField(FieldSpec.builder(ClassName.get(JmsLoggerMetadataAdder.class), JMS_LOGGER_METADATA_ADDER_FIELD)
                        .addAnnotation(Inject.class)
                        .build())
                .addMethod(generateAddRequestDataToMdcMethod(serviceComponent))
                .build();
    }

    /**
     * Generate the addRequestDataToMdc method.
     *
     * @return the {@link MethodSpec} that represents the addRequestDataToMdc method
     */
    private MethodSpec generateAddRequestDataToMdcMethod(final String serviceComponent) {

        final String invocationContextParameter = "invocationContext";

        return MethodSpec.methodBuilder("addRequestDataToMdc")
                .addModifiers(PUBLIC)
                .addAnnotation(AroundInvoke.class)
                .addParameter(ParameterSpec
                        .builder(InvocationContext.class, invocationContextParameter, FINAL)
                        .build())
                .addException(Exception.class)
                .addCode(CodeBlock.builder()
                        .addStatement("return $L.addRequestDataToMdc($L, $S)",
                                JMS_LOGGER_METADATA_ADDER_FIELD,
                                invocationContextParameter,
                                serviceComponent
                        )
                        .build())
                .returns(Object.class)
                .build();
    }
}
