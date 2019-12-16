package uk.gov.justice.services.adapter.messaging;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.core.annotation.Component.EVENT_LISTENER;

import uk.gov.justice.services.framework.system.errors.SystemErrorService;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.messaging.jms.EnvelopeConverter;

import javax.interceptor.InvocationContext;
import javax.jms.TextMessage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

@RunWith(MockitoJUnitRunner.class)
public class ErrorReportExtractorTest {

    @Mock
    private EnvelopeConverter envelopeConverter;

    @Mock
    private SystemErrorService systemErrorService;

    @Mock
    private Logger logger;

    @InjectMocks
    private ErrorReportExtractor errorReportExtractor;

    @Test
    public void shouldCatchAndReportError() throws Exception {

        final InvocationContext invocationContext = mock(InvocationContext.class);
        final Exception exception = new Exception();
        final TextMessage textMessage = mock(TextMessage.class);
        final Object[] parameters = {textMessage};
        final JsonEnvelope jsonEnvelope = mock(JsonEnvelope.class);
        final String jmsMessageId = "jms message id";

        doThrow(exception).when(invocationContext).proceed();
        when(invocationContext.getParameters()).thenReturn(parameters);
        when(textMessage.getJMSMessageID()).thenReturn(jmsMessageId);
        when(envelopeConverter.fromMessage(textMessage)).thenReturn(jsonEnvelope);

        errorReportExtractor.extractAndReportError(invocationContext, EVENT_LISTENER, exception);

        verify(systemErrorService).reportError(jmsMessageId, EVENT_LISTENER, jsonEnvelope, exception);
    }

    @Test
    public void shouldLogServiceReportError() throws Exception {

        final InvocationContext invocationContext = mock(InvocationContext.class);
        final Exception exception = new Exception();
        final Exception reportException = new RuntimeException();
        final TextMessage textMessage = mock(TextMessage.class);
        final Object[] parameters = {textMessage};
        final JsonEnvelope jsonEnvelope = mock(JsonEnvelope.class);
        final String jmsMessageId = "jms message id";

        doThrow(exception).when(invocationContext).proceed();
        when(invocationContext.getParameters()).thenReturn(parameters);
        when(textMessage.getJMSMessageID()).thenReturn(jmsMessageId);
        when(envelopeConverter.fromMessage(textMessage)).thenReturn(jsonEnvelope);
        doThrow(reportException).when(systemErrorService).reportError(jmsMessageId, EVENT_LISTENER, jsonEnvelope, exception);

        errorReportExtractor.extractAndReportError(invocationContext, EVENT_LISTENER, exception);

        verify(logger).error("Failed to extract and report processing error in component: EVENT_LISTENER", reportException);
    }
}