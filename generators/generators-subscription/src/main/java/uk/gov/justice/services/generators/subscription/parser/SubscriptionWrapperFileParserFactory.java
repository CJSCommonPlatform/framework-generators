package uk.gov.justice.services.generators.subscription.parser;

import uk.gov.justice.maven.generator.io.files.parser.FileParserFactory;
import uk.gov.justice.services.common.converter.jackson.ObjectMapperProducer;
import uk.gov.justice.services.generators.commons.helper.PathToUrlResolver;
import uk.gov.justice.services.yaml.YamlFileValidator;
import uk.gov.justice.services.yaml.YamlParser;
import uk.gov.justice.services.yaml.YamlSchemaLoader;
import uk.gov.justice.services.yaml.YamlToJsonObjectConverter;
import uk.gov.justice.subscription.EventSourcesParser;
import uk.gov.justice.subscription.SubscriptionSorter;
import uk.gov.justice.subscription.SubscriptionsDescriptorParser;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SubscriptionWrapperFileParserFactory implements FileParserFactory<SubscriptionWrapper> {

    @Override
    public SubscriptionWrapperFileParser create() {

        final YamlParser yamlParser = new YamlParser();
        final YamlSchemaLoader yamlSchemaLoader = new YamlSchemaLoader();
        final ObjectMapper objectMapper = new ObjectMapperProducer().objectMapper();
        final YamlFileValidator yamlFileValidator = new YamlFileValidator(new YamlToJsonObjectConverter(yamlParser, objectMapper), yamlSchemaLoader);
        final SubscriptionSorter subscriptionSorter = new SubscriptionSorter();

        final EventSourcesParser eventSourcesParser = new EventSourcesParser(yamlParser, yamlFileValidator);
        final PathToUrlResolver pathToUrlResolver = new PathToUrlResolver();
        final EventSourceYamlClasspathFinder eventSourceYamlClasspathFinder = new EventSourceYamlClasspathFinder();

        final EventSourcesFileParser eventSourcesFileParser = new EventSourcesFileParser(eventSourcesParser, pathToUrlResolver, eventSourceYamlClasspathFinder);

        final SubscriptionsDescriptorParser subscriptionsDescriptorParser = new SubscriptionsDescriptorParser(yamlParser, yamlFileValidator, subscriptionSorter);
        final SubscriptionDescriptorFileParser subscriptionDescriptorFileParser = new SubscriptionDescriptorFileParser(subscriptionsDescriptorParser, pathToUrlResolver);

        return new SubscriptionWrapperFileParser(eventSourcesFileParser, subscriptionDescriptorFileParser);
    }
}
