package uk.gov.justice.services.adapter.messaging;

import javax.inject.Inject;
import javax.interceptor.InvocationContext;

public class JmsMessageProcessErrorReporter {

    @Inject
    private ErrorReportExtractor errorReportExtractor;

    public Object catchAndReportError(final InvocationContext invocationContext, final String componentName) throws Exception {

        try {
            return invocationContext.proceed();
        } catch (final Exception e) {
            errorReportExtractor.extractAndReportError(invocationContext, componentName, e);
            throw e;
        }
    }
}
