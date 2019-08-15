package uk.gov.justice.services.clients.unifiedsearch.generator.parser;

import static java.nio.file.Paths.get;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

import uk.gov.justice.services.clients.unifiedsearch.core.domain.Event;
import uk.gov.justice.services.clients.unifiedsearch.core.domain.UnifiedSearchDescriptor;
import uk.gov.justice.services.yaml.YamlValidationException;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

public class UnifiedSearchDescriptorParserTest {

    final Path baseDir = get("src/test/resources/yaml");

    @Test
    public void shouldFailOnIncorrectSubscriptionYaml() {
        final UnifiedSearchDescriptorParser unifiedSearchDescriptorParser = new UnifiedSearchDescriptorFileParserFactory().create();

        final List<Path> paths = asList(get("unified-search-descriptor-missing-service-component.yaml"));
        try {
            unifiedSearchDescriptorParser.parse(baseDir, paths);
            fail();
        } catch (final YamlValidationException re) {
            assertThat(re.getMessage(), containsString("#/unified_search_descriptor: required key [service_component] not found"));
        }
    }

    @Test
    public void shouldParsePathsToYaml() {
        final UnifiedSearchDescriptorParser unifiedSearchDescriptorParser = new UnifiedSearchDescriptorFileParserFactory().create();

        final List<Path> paths = asList(get("unified-search-descriptor.yaml"));
        final Collection<UnifiedSearchDescriptor> unifiedSearchDescriptors = unifiedSearchDescriptorParser.parse(baseDir, paths);

        assertThat(unifiedSearchDescriptors, hasSize(1));
        for (final UnifiedSearchDescriptor unifiedSearchDescriptor : unifiedSearchDescriptors) {
            assertUnifiedSearchDescriptor(unifiedSearchDescriptor);
            assertExampleEvent(unifiedSearchDescriptor);
        }
    }

    private UnifiedSearchDescriptor assertUnifiedSearchDescriptor(final UnifiedSearchDescriptor unifiedSearchDescriptor) {
        assertThat(unifiedSearchDescriptor.getName(), is("example"));
        assertThat(unifiedSearchDescriptor.getService(), is("examplecontext"));
        assertThat(unifiedSearchDescriptor.getServiceComponent(), is("EVENT_INDEXER"));
        assertThat(unifiedSearchDescriptor.getSpecVersion(), is("1.0.0"));
        return unifiedSearchDescriptor;
    }

    private void assertExampleEvent(final UnifiedSearchDescriptor unifiedSearchDescriptor) {
        final List<Event> events = unifiedSearchDescriptor.getEvents();
        assertThat(events.size(), is(2));

        assertThat(events.get(0).getName(), is("example.recipe-added"));
        assertThat(events.get(0).getIndexName(), is("recipe_added_index"));
        assertThat(events.get(0).getTransformerConfig(), is("example.recipe-added-spec.json"));

        assertThat(events.get(1).getName(), is("example.recipe-removed"));
        assertThat(events.get(1).getIndexName(), is("recipe_removed_index"));
        assertThat(events.get(1).getTransformerConfig(), is("example.recipe-removed-spec.json"));
    }

}