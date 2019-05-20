package uk.gov.justice.services.clients.unifiedsearch.core;

public class UnifiedSearchException extends RuntimeException {
    public UnifiedSearchException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public UnifiedSearchException(final String message) {
        super(message);
    }
}
