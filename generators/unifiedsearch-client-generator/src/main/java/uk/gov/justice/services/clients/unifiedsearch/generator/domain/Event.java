package uk.gov.justice.services.clients.unifiedsearch.generator.domain;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Event event = (Event) o;

        if (name != null ? !name.equals(event.name) : event.name != null) return false;
        if (transformerConfig != null ? !transformerConfig.equals(event.transformerConfig) : event.transformerConfig != null)
            return false;
        return indexName != null ? indexName.equals(event.indexName) : event.indexName == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (transformerConfig != null ? transformerConfig.hashCode() : 0);
        result = 31 * result + (indexName != null ? indexName.hashCode() : 0);
        return result;
    }
}
