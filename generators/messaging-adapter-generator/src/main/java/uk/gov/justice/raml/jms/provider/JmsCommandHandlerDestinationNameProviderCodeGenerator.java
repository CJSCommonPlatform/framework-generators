package uk.gov.justice.raml.jms.provider;

import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static com.squareup.javapoet.TypeSpec.classBuilder;
import static javax.lang.model.element.Modifier.PUBLIC;
import static uk.gov.justice.raml.jms.core.ClassNameFactory.JMS_HANDLER_DESTINATION_NAME_PROVIDER;

import uk.gov.justice.raml.jms.core.ClassNameFactory;
import uk.gov.justice.services.generators.commons.helper.MessagingResourceUri;
import uk.gov.justice.services.messaging.jms.JmsCommandHandlerDestinationNameProvider;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.model.Resource;

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

    public TypeSpec generate(final Resource resource, final ClassNameFactory classNameFactory) {

        final ClassName className = classNameFactory.classNameFor(JMS_HANDLER_DESTINATION_NAME_PROVIDER);

        return classBuilder(className)
                .addModifiers(PUBLIC)
                .addSuperinterface(JmsCommandHandlerDestinationNameProvider.class)
                .addMethod(createJmsHandlerDestinationNameMethod(resource))
                .build();

    }

    private MethodSpec createJmsHandlerDestinationNameMethod(final Resource resource) {

        final MessagingResourceUri resourceUri = new MessagingResourceUri(resource.getUri());

        return methodBuilder("destinationName")
                .addModifiers(PUBLIC)
                .addAnnotation(Override.class)
                .addStatement("return \"$L\"", resourceUri.destinationName())
                .returns(String.class)
                .build();
    }
}
