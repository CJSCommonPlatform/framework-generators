package uk.gov.justice.services.clients.unifiedsearch.core;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class UnifiedSearchExceptionTest {
    @Test
    public void shouldCreateInstanceOfUnifiedSearchExceptionWithMessage() throws Exception {
        final UnifiedSearchException exception = new UnifiedSearchException("Test message", new Exception());
        assertThat(exception.getMessage(), is("Test message"));
        assertThat(exception, instanceOf(Exception.class));
    }

}