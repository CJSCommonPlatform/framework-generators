package uk.gov.justice.services.clients.unifiedsearch.core;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;

import uk.gov.justice.services.clients.unifiedsearch.core.domain.UnifiedSearchDescriptor;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class UnifiedSearchTransformerCacheUtilsTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldThrowExceptionWhenTransformerPathsAreMoreThanOne() throws MalformedURLException {

        exception.expect(UnifiedSearchException.class);
        exception.expectMessage("You must declare single transformer");

        final List<URL> urls = asList(new URL("http://test.com/"), new URL("http://test.com/"));
        final UnifiedSearchTransformerCacheUtils unifiedSearchTransformerCacheUtils = new UnifiedSearchTransformerCacheUtils();

        unifiedSearchTransformerCacheUtils.verifyTransformerPaths(urls);
    }

    @Test
    public void shouldThrowExceptionWhenTransformerPathsAreEmpty() throws MalformedURLException {

        exception.expect(UnifiedSearchException.class);
        exception.expectMessage("You must declare single transformer");

        final List<URL> urls = new ArrayList<>();
        final UnifiedSearchTransformerCacheUtils unifiedSearchTransformerCacheUtils = new UnifiedSearchTransformerCacheUtils();

        unifiedSearchTransformerCacheUtils.verifyTransformerPaths(urls);
    }


    @Test
    public void shouldThrowExceptionWhenDescriptorsAreMoreThanOne() throws MalformedURLException {

        exception.expect(UnifiedSearchException.class);
        exception.expectMessage("You must declare single unified-search-descriptor.yaml");

        final List<UnifiedSearchDescriptor> unifiedSearchDescriptorList = asList(mock(UnifiedSearchDescriptor.class), mock(UnifiedSearchDescriptor.class));
        final UnifiedSearchTransformerCacheUtils unifiedSearchTransformerCacheUtils = new UnifiedSearchTransformerCacheUtils();

        unifiedSearchTransformerCacheUtils.verifyUnifiedSearchDescriptor(unifiedSearchDescriptorList);
    }

    @Test
    public void shouldThrowExceptionWhenDescriptorsAreEmpty() throws MalformedURLException {

        exception.expect(UnifiedSearchException.class);
        exception.expectMessage("You must declare single unified-search-descriptor.yaml");

        final List<UnifiedSearchDescriptor> unifiedSearchDescriptorList = new ArrayList<>();
        final UnifiedSearchTransformerCacheUtils unifiedSearchTransformerCacheUtils = new UnifiedSearchTransformerCacheUtils();

        unifiedSearchTransformerCacheUtils.verifyUnifiedSearchDescriptor(unifiedSearchDescriptorList);
    }
}