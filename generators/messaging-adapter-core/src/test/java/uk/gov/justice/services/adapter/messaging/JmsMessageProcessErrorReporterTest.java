package uk.gov.justice.services.adapter.messaging;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.core.annotation.Component.COMMAND_API;

import javax.interceptor.InvocationContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class JmsMessageProcessErrorReporterTest {

    @Mock
    private ErrorReportExtractor errorReportExtractor;

    @InjectMocks
    private JmsMessageProcessErrorReporter jmsMessageProcessErrorReporter;

    @Test
    public void shouldProcessNormally() throws Exception {

        final Object expected = mock(Object.class);
        final InvocationContext invocationContext = mock(InvocationContext.class);

        when(invocationContext.proceed()).thenReturn(expected);

        final Object result = jmsMessageProcessErrorReporter.catchAndReportError(invocationContext, COMMAND_API);

        assertThat(result, is(expected));
    }

    @Test
    public void shouldCatchAndReportError() throws Exception {

        final InvocationContext invocationContext = mock(InvocationContext.class);
        final Exception exception = new Exception();

        doThrow(exception).when(invocationContext).proceed();

        try {
            jmsMessageProcessErrorReporter.catchAndReportError(invocationContext, COMMAND_API);
        } catch (final Exception e) {
            assertThat(e, is(exception));
        }

        verify(errorReportExtractor).extractAndReportError(invocationContext, COMMAND_API, exception);
    }
}