package uk.gov.justice.services.clients.unifiedsearch.generator;

import static uk.gov.justice.services.core.enveloper.Enveloper.envelop;

import uk.gov.justice.services.clients.unifiedsearch.core.UnifiedSearchTransformerCache;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.unifiedsearch.TransformerApi;
import uk.gov.justice.services.unifiedsearch.UnifiedSearchIndexer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;

@ApplicationScoped
public class UnifiedSearchAdapter {

    @Inject
    private TransformerApi transformerApi;

    @Inject
    private UnifiedSearchTransformerCache unifiedSearchTransformerCache;

    public void index(final JsonEnvelope jsonEnvelope, final UnifiedSearchIndexer indexer) {
        final String transformerConfig = unifiedSearchTransformerCache.getTransformerConfigBy(jsonEnvelope.metadata().name());
        final JsonObject transformedJson = transformerApi.transformWithJolt(transformerConfig, jsonEnvelope.payloadAsJsonObject());

        indexer.indexData(envelop(transformedJson)
                .withName(jsonEnvelope.metadata().name())
                .withMetadataFrom(jsonEnvelope));
    }
}

