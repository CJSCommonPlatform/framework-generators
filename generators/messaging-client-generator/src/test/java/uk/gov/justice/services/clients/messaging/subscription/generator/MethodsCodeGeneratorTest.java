package uk.gov.justice.services.clients.messaging.subscription.generator;

import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import uk.gov.justice.subscription.domain.eventsource.EventSourceDefinition;
import uk.gov.justice.subscription.domain.subscriptiondescriptor.Event;
import uk.gov.justice.subscription.domain.subscriptiondescriptor.Subscription;

import java.util.List;

import com.squareup.javapoet.MethodSpec;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MethodsCodeGeneratorTest {

    @Mock
    private MethodCodeGenerator methodCodeGenerator;

    @InjectMocks
    private MethodsCodeGenerator methodsCodeGenerator;

    @Test
    public void shouldGenerateMethodsForEachEventInASubscritpion() {

        final Event event_1 = new Event("test.event-1", "http://justice.gov.uk/json/schema/test-1");
        final Event event_2 = new Event("test.event-2", "http://justice.gov.uk/json/schema/test-2");

        final Subscription subscription = new Subscription("subscription", asList(event_1, event_2), "event-source", 1);

        final EventSourceDefinition eventSourceDefinition = mock(EventSourceDefinition.class);
        final ClassNameAndMethodNameFactory classNameAndMethodNameFactory = mock(ClassNameAndMethodNameFactory.class);

        final MethodSpec methodSpec_1 = methodBuilder("spec_1").build();
        final MethodSpec methodSpec_2 = methodBuilder("spec_2").build();

        when(methodCodeGenerator.methodFor(event_1, eventSourceDefinition, classNameAndMethodNameFactory)).thenReturn(methodSpec_1);
        when(methodCodeGenerator.methodFor(event_2, eventSourceDefinition, classNameAndMethodNameFactory)).thenReturn(methodSpec_2);

        final List<MethodSpec> methodSpecs = methodsCodeGenerator.methodsFor(subscription, eventSourceDefinition, classNameAndMethodNameFactory);

        assertThat(methodSpecs.size(), is(2));
        assertThat(methodSpecs.get(0), is(methodSpec_1));
        assertThat(methodSpecs.get(1), is(methodSpec_2));
    }
}