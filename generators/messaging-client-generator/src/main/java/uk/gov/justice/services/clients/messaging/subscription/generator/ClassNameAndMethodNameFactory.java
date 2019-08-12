package uk.gov.justice.services.clients.messaging.subscription.generator;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static uk.gov.justice.services.generators.commons.helper.Names.buildJavaFriendlyName;

import java.util.stream.Stream;

import com.squareup.javapoet.ClassName;

public class ClassNameAndMethodNameFactory {

    public static final String REMOTE_MESSAGING_CLIENT = "MessagingClient";

    private final String basePackageName;
    private final String contextName;
    private final String componentName;
    private final String jmsUri;

    public ClassNameAndMethodNameFactory(final String basePackageName,
                                         final String contextName,
                                         final String componentName,
                                         final String jmsUri) {
        this.basePackageName = basePackageName;
        this.contextName = contextName;
        this.componentName = componentName;
        this.jmsUri = jmsUri;
    }

    /**
     * Convert given URI and component to a camel cased class name
     *
     * @param classNameSuffix class name suffix identifier
     * @return Java Poet class name
     */
    public ClassName classNameFor(final String classNameSuffix) {

        final String simpleName = buildJavaFriendlyName(format("Remote_%s2_%s_%s_%s", componentName.toLowerCase(), jmsUriToClassName(jmsUri), contextName, classNameSuffix));

        return ClassName.get(basePackageName, simpleName);
    }

    public String postMethodNameFor(final String name) {
        return "post" +
                buildJavaFriendlyName(contextName) +
                componentNameToClassName(componentName) +
                buildJavaFriendlyName(name);
    }

    private String jmsUriToClassName(final String jmsUri) {

        final String queueName = jmsUri.split(":")[2];

        return buildJavaFriendlyName(queueName);
    }

    private String componentNameToClassName(final String componentName) {

        return Stream.of(componentName.split("_"))
                .map(token -> capitalize(token.toLowerCase()))
                .collect(joining());
    }
}
