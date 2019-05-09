package uk.gov.justice.subscription.jms.parser;

import uk.gov.justice.maven.generator.io.files.parser.FileParserFactory;
import uk.gov.justice.services.common.converter.jackson.ObjectMapperProducer;
import uk.gov.justice.services.generators.commons.helper.PathToUrlResolver;
import uk.gov.justice.services.yaml.YamlFileValidator;
import uk.gov.justice.services.yaml.YamlParser;
import uk.gov.justice.services.yaml.YamlSchemaLoader;
import uk.gov.justice.services.yaml.YamlToJsonObjectConverter;
import uk.gov.justice.subscription.EventSourcesParser;
import uk.gov.justice.subscription.SubscriptionHelper;
import uk.gov.justice.subscription.SubscriptionsDescriptorParser;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SubscriptionWrapperFileParserFactory implements FileParserFactory<SubscriptionWrapper> {

    @Override
    public SubscriptionWrapperFileParser create() {
        final YamlParser yamlParser = new YamlParser();
        final YamlSchemaLoader yamlSchemaLoader = new YamlSchemaLoader();
        final ObjectMapper objectMapper = new ObjectMapperProducer().objectMapper();
        final YamlFileValidator yamlFileValidator = new YamlFileValidator(new YamlToJsonObjectConverter(yamlParser, objectMapper), yamlSchemaLoader);
        final SubscriptionHelper subscriptionHelper = new SubscriptionHelper();

        final EventSourcesParser eventSourcesParser = new EventSourcesParser(yamlParser, yamlFileValidator);
        final PathToUrlResolver pathToUrlResolver = new PathToUrlResolver();

        final EventSourcesFileParser eventSourcesFileParser = new EventSourcesFileParser(eventSourcesParser, pathToUrlResolver);

        final SubscriptionsDescriptorParser subscriptionsDescriptorParser = new SubscriptionsDescriptorParser(yamlParser, yamlFileValidator,subscriptionHelper);
        final SubscriptionDescriptorFileParser subscriptionDescriptorFileParser = new SubscriptionDescriptorFileParser(subscriptionsDescriptorParser, pathToUrlResolver);

        return new SubscriptionWrapperFileParser(eventSourcesFileParser, subscriptionDescriptorFileParser);
    }
}
