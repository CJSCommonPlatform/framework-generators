package uk.gov.justice.services.clients.unifiedsearch.generator;

import static org.mockito.Answers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import uk.gov.justice.services.clients.unifiedsearch.core.UnifiedSearchTransformerCache;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.unifiedsearch.TransformerApi;
import uk.gov.justice.services.unifiedsearch.UnifiedSearchIndexer;

import javax.json.JsonObject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UnifiedSearchAdapterTest {

    @Mock(answer = RETURNS_DEEP_STUBS)
    private JsonEnvelope jsonEnvelope;

    @Mock
    private JsonObject transformedEvent;

    @Mock
    private UnifiedSearchIndexer unifiedSearchIndexer;

    @Mock
    private TransformerApi transformerApi;

    @Mock
    private UnifiedSearchTransformerCache unifiedSearchTransformerCache;

    @InjectMocks
    private UnifiedSearchAdapter unifiedSearchAdapter;

    @Test
    public void verifyIndexerIsCalled() {
        final String transformerOperations = "";
        final String eventName = "test";

        when(jsonEnvelope.metadata().name()).thenReturn(eventName);
        when(unifiedSearchTransformerCache.getTransformerConfigBy(eventName)).thenReturn(transformerOperations);
        when(transformerApi.transformWithJolt(transformerOperations, jsonEnvelope.payloadAsJsonObject())).thenReturn(transformedEvent);

        unifiedSearchAdapter.index(jsonEnvelope, unifiedSearchIndexer);

        verify(unifiedSearchIndexer).indexData(transformedEvent);
    }
}