package uk.gov.justice.services.clients.messaging.subscription.generator;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import uk.gov.justice.subscription.domain.eventsource.EventSourceDefinition;
import uk.gov.justice.subscription.domain.subscriptiondescriptor.Event;
import uk.gov.justice.subscription.domain.subscriptiondescriptor.Subscription;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RemoteMessagingClientCodeGeneratorTest {

    private static final String EXPECTED_CLASS =
            "@uk.gov.justice.services.core.annotation.Remote\n" +
                    "@uk.gov.justice.services.core.annotation.FrameworkComponent(\"EVENT_LISTENER\")\n" +
                    "public final class TestClassName {\n" +
                    "  private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(org.test.TestClassName.class);\n\n" +
                    "  @javax.inject.Inject\n" +
                    "  uk.gov.justice.services.messaging.jms.JmsEnvelopeSender sender;\n\n" +
                    "  @javax.inject.Inject\n" +
                    "  uk.gov.justice.services.messaging.logging.TraceLogger traceLogger;\n" +
                    "}\n";

    @Mock
    private MethodsCodeGenerator methodsCodeGenerator;

    @InjectMocks
    private RemoteMessagingClientCodeGenerator remoteMessagingClientCodeGenerator;

    @Test
    public void shouldGenerateRemoteMessagingClientTypeSpec() {

        final Event event_1 = new Event("test.event-1", "http://justice.gov.uk/json/schema/test-1");
        final Event event_2 = new Event("test.event-2", "http://justice.gov.uk/json/schema/test-2");

        final Subscription subscription = new Subscription("subscription", asList(event_1, event_2), "event-source", 1);

        final EventSourceDefinition eventSourceDefinition = mock(EventSourceDefinition.class);
        final ClassNameAndMethodNameFactory classNameAndMethodNameFactory = mock(ClassNameAndMethodNameFactory.class);

        when(classNameAndMethodNameFactory.classNameFor(ClassNameAndMethodNameFactory.REMOTE_MESSAGING_CLIENT)).thenReturn(ClassName.get("org.test", "TestClassName"));
        when(methodsCodeGenerator.methodsFor(subscription, eventSourceDefinition, classNameAndMethodNameFactory)).thenReturn(emptyList());

        final TypeSpec typeSpec = remoteMessagingClientCodeGenerator.classFor(subscription, eventSourceDefinition, "EVENT_LISTENER", classNameAndMethodNameFactory);

        assertThat(typeSpec.toString(), is(EXPECTED_CLASS));
    }
}