package uk.gov.justice.services.clients.unifiedsearch.core;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.net.URL;
import java.nio.file.Path;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class UnifiedSearchFileFinderTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldFindAllSubscriptionDescriptorsOnTheClasspathWhichHaveTheCorrectName() throws Exception {

        final UnifiedSearchFileFinder unifiedSearchFileFinder = new UnifiedSearchFileFinder();

        final List<Path> urls = unifiedSearchFileFinder.getUnifiedSearchDescriptor();

        assertThat(urls.size(), is(1));

        assertThat(urls.get(0).toString(), endsWith("/yaml/unified-search-descriptor.yaml"));
    }

    @Test
    public void shouldFindTransformerPaths() throws Exception {

        final UnifiedSearchFileFinder unifiedSearchFileFinder = new UnifiedSearchFileFinder();
        final List<URL> urls = unifiedSearchFileFinder.getTransformerPaths("test-spec1.json");

        assertThat(urls.size(), is(1));

        assertThat(urls.get(0).toString(), endsWith("/transformer/test-spec1.json"));

    }

    @Test
    public void shouldThrowExceptionWhenTransformerNotFound() throws Exception {

        exception.expect(UnifiedSearchException.class);
        exception.expectMessage("Unable to find file on classpath: non-existent-spec.json");

        final UnifiedSearchFileFinder unifiedSearchFileFinder = new UnifiedSearchFileFinder();
        unifiedSearchFileFinder.getTransformerPaths("non-existent-spec.json");
    }
}