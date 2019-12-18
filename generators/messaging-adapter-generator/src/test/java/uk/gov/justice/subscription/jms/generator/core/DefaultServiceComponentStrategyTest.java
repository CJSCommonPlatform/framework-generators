package uk.gov.justice.subscription.jms.generator.core;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import uk.gov.justice.subscription.domain.subscriptiondescriptor.Event;
import uk.gov.justice.subscription.domain.subscriptiondescriptor.Subscription;
import uk.gov.justice.subscription.jms.generator.ClassNameFactory;

import java.util.List;
import java.util.Optional;

import com.squareup.javapoet.AnnotationSpec;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DefaultServiceComponentStrategyTest {

    @Spy
    private ClassNameFactory classNameFactory = new ClassNameFactory("base.package", "my-context", "EVENT_LISTENER", "jms:topic:my-context.handler.command");

    @Mock
    private Subscription subscription;

    @InjectMocks
    private DefaultServiceComponentStrategy defaultComponentStrategy;

    @Test
    public void shouldCreateAnnotationSpecIfThereAreEvents() {

        final Event event = mock(Event.class);
        final List<Event> events = singletonList(event);

        when(subscription.getEvents()).thenReturn(events);

        final Optional<AnnotationSpec> annotationSpec = defaultComponentStrategy.createAnnotationSpec();

        assertThat(annotationSpec.isPresent(), is(true));
        assertThat(annotationSpec.get().toString(), is("@javax.interceptor.Interceptors({base.package.MyContextEventListenerMyContextHandlerCommandJmsLoggerMetadataInterceptor.class, uk.gov.justice.services.adapter.messaging.JsonSchemaValidationInterceptor.class})"));
    }

    @Test
    public void shouldReturnOptionalEmptyIfThereAreNoEvents() {

        when(subscription.getEvents()).thenReturn(emptyList());

        final Optional<AnnotationSpec> annotationSpec = defaultComponentStrategy.createAnnotationSpec();

        assertThat(annotationSpec.isPresent(), is(false));
    }
}