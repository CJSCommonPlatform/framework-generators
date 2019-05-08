package uk.gov.justice.services.clients.unifiedsearch.generator;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import uk.gov.justice.json.api.TransformerApi;
import uk.gov.justice.services.unifiedsearch.UnifiedSearchIndexer;

import javax.json.JsonObject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UnifiedSearchAdapterTest {

    @Mock
    private JsonObject event;

    @Mock
    private JsonObject transformedEvent;

    @Mock
    private UnifiedSearchIndexer unifiedSearchIndexer;

    @Mock
    private TransformerApi transformerApi;

    @InjectMocks
    private UnifiedSearchAdapter unifiedSearchAdapter;

    @Test
    public void verfiyIndexerIsCalled() {
        final String transformerOperations = "";

        when(transformerApi.transformWithJolt(transformerOperations, event)).thenReturn(transformedEvent);

        unifiedSearchAdapter.index(event, unifiedSearchIndexer, transformerOperations);

        verify(unifiedSearchIndexer).indexData(transformedEvent);
    }
}