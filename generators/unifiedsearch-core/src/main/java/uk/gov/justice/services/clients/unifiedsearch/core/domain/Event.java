package uk.gov.justice.services.clients.unifiedsearch.core.domain;

public class Event {
    private final String name;
    private final String transformerConfig;
    private final String indexName;

    public Event(final String name, final String transformerConfig, final String indexName) {
        this.name = name;
        this.transformerConfig = transformerConfig;
        this.indexName = indexName;
    }


    public String getName() {
        return name;
    }

    public String getTransformerConfig() {
        return transformerConfig;
    }

    public String getIndexName() {
        return indexName;
    }

}
