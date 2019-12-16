package uk.gov.justice.services.adapter.messaging;

import uk.gov.justice.services.framework.system.errors.SystemErrorService;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.messaging.jms.EnvelopeConverter;

import javax.inject.Inject;
import javax.interceptor.InvocationContext;
import javax.jms.TextMessage;

import org.slf4j.Logger;

public class ErrorReportExtractor {

    @Inject
    private EnvelopeConverter envelopeConverter;

    @Inject
    private SystemErrorService systemErrorService;

    @Inject
    private Logger logger;

    public void extractAndReportError(final InvocationContext invocationContext, final String componentName, final Exception e) {

        try {
            final TextMessage message = (TextMessage) invocationContext.getParameters()[0];
            final String jmsMessageID = message.getJMSMessageID();
            final JsonEnvelope jsonEnvelope = envelopeConverter.fromMessage(message);

            systemErrorService.reportError(jmsMessageID, componentName, jsonEnvelope, e);
        } catch (final Exception reportException) {
            logger.error(String.format("Failed to extract and report processing error in component: %s", componentName), reportException);
        }
    }
}
