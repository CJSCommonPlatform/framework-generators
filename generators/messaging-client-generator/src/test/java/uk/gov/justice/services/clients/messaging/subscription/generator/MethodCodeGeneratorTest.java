package uk.gov.justice.services.clients.messaging.subscription.generator;

import static java.util.Optional.empty;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import uk.gov.justice.subscription.domain.eventsource.EventSourceDefinition;
import uk.gov.justice.subscription.domain.eventsource.Location;
import uk.gov.justice.subscription.domain.subscriptiondescriptor.Event;

import java.util.List;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MethodCodeGeneratorTest {

    @InjectMocks
    private MethodCodeGenerator methodCodeGenerator;

    @Test
    public void shouldCreateMethodSpecForEvent() {

        final Event event = new Event("test.event", "http://justice.gov.uk/json/schema/test");
        final EventSourceDefinition eventSourceDefinition = new EventSourceDefinition("test.service", true, new Location("jms:topic:test.event", empty(), empty()));
        final ClassNameAndMethodNameFactory classNameAndMethodNameFactory = mock(ClassNameAndMethodNameFactory.class);
        final String methodName = "postTestComponentTestEvent";

        when(classNameAndMethodNameFactory.postMethodNameFor("test.event")).thenReturn(methodName);

        final MethodSpec methodSpec = methodCodeGenerator.methodFor(event, eventSourceDefinition, classNameAndMethodNameFactory);

        assertThat(methodSpec.name, is(methodName));

        final List<AnnotationSpec> annotations = methodSpec.annotations;
        assertThat(annotations.size(), is(1));
        assertThat(annotations.get(0).toString(), is("@uk.gov.justice.services.core.annotation.Handles(\"test.event\")"));

        final List<ParameterSpec> parameters = methodSpec.parameters;
        assertThat(parameters.size(), is(1));
        assertThat(parameters.get(0).toString(), is("final uk.gov.justice.services.messaging.JsonEnvelope envelope"));

        assertThat(methodSpec.code.toString(), is("traceLogger.trace(LOGGER, () -> String.format(\"Handling remote request: %s\", envelope));\nsender.send(envelope, \"test.event\");\n"));
    }
}