package uk.gov.justice.services.clients.messaging.subscription.generator;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import com.squareup.javapoet.ClassName;
import org.junit.Test;

public class ClassNameAndMethodNameFactoryTest {

    private static final String BASE_PACKAGE_NAME = "base.package";
    private static final String CONTEXT_NAME = "my-context";
    private static final String COMPONENT_NAME = "EVENT_LISTENER";
    private static final String JMS_URI = "jms:topic:my-context.handler.command";

    @Test
    public void shouldCreateClassNameFromBaseUriResourceUriAndClassNameSuffixForEventListener() {

        final ClassNameAndMethodNameFactory classNameAndMethodNameFactory = createClassNameAndMethodFactoryWithDefaults();

        final ClassName className = classNameAndMethodNameFactory.classNameFor("ClassNameSuffix");

        assertThat(className.packageName(), is(BASE_PACKAGE_NAME));
        assertThat(className.simpleName(), is("RemoteEventListener2MyContextHandlerCommandMyContextClassNameSuffix"));
    }

    @Test
    public void shouldReturnMethodName() {

        final ClassNameAndMethodNameFactory classNameAndMethodNameFactory = createClassNameAndMethodFactoryWithDefaults();

        final String methodName = classNameAndMethodNameFactory.postMethodNameFor("people.event");

        assertThat(methodName, is("postMyContextEventListenerPeopleEvent"));
    }

    private ClassNameAndMethodNameFactory createClassNameAndMethodFactoryWithDefaults() {

        return new ClassNameAndMethodNameFactory(
                BASE_PACKAGE_NAME,
                CONTEXT_NAME,
                COMPONENT_NAME,
                JMS_URI);
    }
}