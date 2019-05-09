package uk.gov.justice.services.generators.commons.helper;

public class FileParserException extends RuntimeException {

    public FileParserException(final String message) {
        super(message);
    }

    public FileParserException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
