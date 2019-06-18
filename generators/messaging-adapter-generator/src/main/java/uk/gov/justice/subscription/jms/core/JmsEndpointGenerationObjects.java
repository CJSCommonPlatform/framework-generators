package uk.gov.justice.subscription.jms.core;

import static org.raml.model.ActionType.POST;

import uk.gov.justice.raml.jms.core.JmsEndpointGenerator;
import uk.gov.justice.raml.jms.validator.BaseUriRamlValidator;
import uk.gov.justice.services.generators.commons.mapping.SchemaIdParser;
import uk.gov.justice.services.generators.commons.mapping.SubscriptionMediaTypeToSchemaIdGenerator;
import uk.gov.justice.services.generators.commons.validator.CompositeRamlValidator;
import uk.gov.justice.services.generators.commons.validator.ContainsActionsRamlValidator;
import uk.gov.justice.services.generators.commons.validator.ContainsResourcesRamlValidator;
import uk.gov.justice.services.generators.commons.validator.RequestContentTypeRamlValidator;
import uk.gov.justice.subscription.jms.interceptor.EventFilterInterceptorCodeGenerator;
import uk.gov.justice.subscription.jms.interceptor.EventInterceptorChainProviderCodeGenerator;
import uk.gov.justice.subscription.jms.interceptor.EventValidationInterceptorCodeGenerator;
import uk.gov.justice.subscription.jms.interceptor.JmsLoggerMetadataInterceptorCodeGenerator;
import uk.gov.justice.subscription.jms.provider.JmsCommandHandlerDestinationNameProviderCodeGenerator;

public class JmsEndpointGenerationObjects {

    public JmsEndpointGenerator jmsEndpointGenerator() {
        return new JmsEndpointGenerator();
    }

    public CompositeRamlValidator compositeRamlValidator() {
        return new CompositeRamlValidator(
                containsResourcesRamlValidator(),
                containsActionsRamlValidator(),
                requestContentTypeRamlValidator(),
                baseUriRamlValidator()
        );
    }

    public ContainsResourcesRamlValidator containsResourcesRamlValidator() {
        return new ContainsResourcesRamlValidator();
    }

    public ContainsActionsRamlValidator containsActionsRamlValidator() {
        return new ContainsActionsRamlValidator();
    }

    public RequestContentTypeRamlValidator requestContentTypeRamlValidator() {
        return new RequestContentTypeRamlValidator(POST);
    }

    public BaseUriRamlValidator baseUriRamlValidator() {
        return new BaseUriRamlValidator();
    }

    public SubscriptionJmsEndpointGenerator subscriptionJmsEndpointGenerator() {
        return new SubscriptionJmsEndpointGenerator(
                messageListenerCodeGenerator(),
                eventFilterCodeGenerator(),
                subscriptionMediaTypeToSchemaIdGenerator(),
                eventFilterInterceptorCodeGenerator(),
                eventValidationInterceptorCodeGenerator(),
                eventListenerInterceptorChainProviderCodeGenerator(),
                jmsLoggerMetadataInterceptorCodeGenerator(),
                jmsCommandHandlerDestinationNameProviderCodeGenerator());
    }

    public MessageListenerCodeGenerator messageListenerCodeGenerator() {
        return new MessageListenerCodeGenerator();
    }

    public EventFilterCodeGenerator eventFilterCodeGenerator() {
        return new EventFilterCodeGenerator();
    }

    public SubscriptionMediaTypeToSchemaIdGenerator subscriptionMediaTypeToSchemaIdGenerator() {
        return new SubscriptionMediaTypeToSchemaIdGenerator();
    }

    public EventFilterInterceptorCodeGenerator eventFilterInterceptorCodeGenerator() {
        return new EventFilterInterceptorCodeGenerator();
    }

    public EventValidationInterceptorCodeGenerator eventValidationInterceptorCodeGenerator() {
        return new EventValidationInterceptorCodeGenerator();
    }

    public EventInterceptorChainProviderCodeGenerator eventListenerInterceptorChainProviderCodeGenerator() {
        return new EventInterceptorChainProviderCodeGenerator();
    }

    public JmsLoggerMetadataInterceptorCodeGenerator jmsLoggerMetadataInterceptorCodeGenerator() {
        return new JmsLoggerMetadataInterceptorCodeGenerator();
    }

    public SchemaIdParser schemaIdParser() {
        return new SchemaIdParser();
    }

    public JmsCommandHandlerDestinationNameProviderCodeGenerator jmsCommandHandlerDestinationNameProviderCodeGenerator() {
        return new JmsCommandHandlerDestinationNameProviderCodeGenerator();
    }
}
