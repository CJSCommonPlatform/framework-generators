package uk.gov.justice.services.clients.unifiedsearch.generator;

import uk.gov.justice.services.unifiedsearch.TransformerApi;
import uk.gov.justice.services.unifiedsearch.UnifiedSearchIndexer;

import javax.inject.Inject;
import javax.json.JsonObject;

public class UnifiedSearchAdapter {

    @Inject
    private TransformerApi transformerApi;

    public void index(final JsonObject event,
                      final UnifiedSearchIndexer indexer,
                      final String transformerConfig) {

        final JsonObject transformedJson = transformerApi.transformWithJolt(transformerConfig, event);

        indexer.indexData(transformedJson);
    }
}

