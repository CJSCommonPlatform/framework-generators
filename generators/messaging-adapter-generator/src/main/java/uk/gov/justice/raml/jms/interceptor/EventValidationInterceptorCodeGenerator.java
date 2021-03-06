package uk.gov.justice.raml.jms.interceptor;

import static com.squareup.javapoet.ClassName.get;
import static com.squareup.javapoet.TypeSpec.classBuilder;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;
import static uk.gov.justice.raml.jms.core.ClassNameFactory.EVENT_FILTER;
import static uk.gov.justice.raml.jms.core.ClassNameFactory.EVENT_VALIDATION_INTERCEPTOR;

import uk.gov.justice.raml.jms.core.ClassNameFactory;
import uk.gov.justice.services.adapter.messaging.JsonSchemaValidationInterceptor;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

/**
 * Should generate a custom EventValidationInterceptor that uses a custom Event Filter. Something like this:
 *
 * <pre>
 *  {@code
 *
 *     import static uk.gov.justice.services.messaging.jms.HeaderConstants.JMS_HEADER_CPPNAME;
 *
 *     public class MyCustomEventValidationInterceptor extends JsonSchemaValidationInterceptor {
 *
 *         @literal @Inject
 *         private MyCustomEventFilter eventFilter;
 *
 *         @literal @Override
 *         public boolean shouldValidate(final TextMessage message) throws JMSException {
 *             final String messageName = message.getStringProperty(JMS_HEADER_CPPNAME);
 *             return eventFilter.accepts(messageName);
 *         }
 *     }
 *
 * }
 * </pre>
 */
public class EventValidationInterceptorCodeGenerator {

    private final EventFilterFieldCodeGenerator eventFilterFieldCodeGenerator = new EventFilterFieldCodeGenerator();

    public TypeSpec generate(final ClassNameFactory classNameFactory) {

        final ClassName className = classNameFactory.classNameFor(EVENT_VALIDATION_INTERCEPTOR);
        final ClassName eventFilterClassName = classNameFactory.classNameFor(EVENT_FILTER);

        return classBuilder(className)
                .addModifiers(PUBLIC)
                .superclass(JsonSchemaValidationInterceptor.class)
                .addField(eventFilterFieldCodeGenerator.createEventFilterField(eventFilterClassName))
                .addMethod(createShouldValidateMethod())
                .build();
    }

    private MethodSpec createShouldValidateMethod() {

        final ClassName headerConstants = get("uk.gov.justice.services.messaging.jms", "HeaderConstants");

        return MethodSpec.methodBuilder("shouldValidate")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addException(JMSException.class)
                .addParameter(TextMessage.class, "textMessage", FINAL)
                .returns(boolean.class)
                .addStatement("final String messageName = textMessage.getStringProperty($T.JMS_HEADER_CPPNAME)", headerConstants)
                .addStatement("return eventFilter.accepts(messageName)")
                .build();
    }
}
