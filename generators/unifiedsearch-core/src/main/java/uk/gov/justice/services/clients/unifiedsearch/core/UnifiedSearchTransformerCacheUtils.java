package uk.gov.justice.services.clients.unifiedsearch.core;

import uk.gov.justice.services.clients.unifiedsearch.core.domain.UnifiedSearchDescriptor;

import java.net.URL;
import java.util.Collection;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UnifiedSearchTransformerCacheUtils {

    public void verifyTransformerPaths(final List<URL> transformerPaths) {
        if (transformerPaths.isEmpty() || transformerPaths.size() > 1) {
            throw new UnifiedSearchException("You must declare single transformer");
        }
    }

    public void verifyUnifiedSearchDescriptor(final Collection<UnifiedSearchDescriptor> unifiedSearchDescriptors) {
        if (unifiedSearchDescriptors.isEmpty() || unifiedSearchDescriptors.size() > 1) {
            throw new UnifiedSearchException("You must declare single unified-search-descriptor.yaml");
        }
    }
}
