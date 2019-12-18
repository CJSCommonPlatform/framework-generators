package uk.gov.justice.subscription.jms.generator;

import static org.raml.model.ActionType.POST;

import uk.gov.justice.raml.jms.core.JmsEndpointGenerator;
import uk.gov.justice.raml.jms.validator.BaseUriRamlValidator;
import uk.gov.justice.services.generators.commons.mapping.SchemaIdParser;
import uk.gov.justice.services.generators.commons.mapping.SubscriptionMediaTypeToSchemaIdGenerator;
import uk.gov.justice.services.generators.commons.validator.CompositeRamlValidator;
import uk.gov.justice.services.generators.commons.validator.ContainsActionsRamlValidator;
import uk.gov.justice.services.generators.commons.validator.ContainsResourcesRamlValidator;
import uk.gov.justice.services.generators.commons.validator.RequestContentTypeRamlValidator;
import uk.gov.justice.subscription.domain.subscriptiondescriptor.Subscription;
import uk.gov.justice.subscription.jms.core.SubscriptionJmsEndpointGenerator;
import uk.gov.justice.subscription.jms.generator.component.ServiceComponentGeneratorTypesFactory;
import uk.gov.justice.subscription.jms.generator.core.EventServiceComponentStrategy;
import uk.gov.justice.subscription.jms.generator.core.MessageListenerCodeGenerator;
import uk.gov.justice.subscription.jms.generator.framework.EventFilterCodeGenerator;
import uk.gov.justice.subscription.jms.generator.framework.EventFilterInterceptorCodeGenerator;
import uk.gov.justice.subscription.jms.generator.framework.EventInterceptorChainProviderCodeGenerator;
import uk.gov.justice.subscription.jms.generator.framework.EventValidationInterceptorCodeGenerator;
import uk.gov.justice.subscription.jms.generator.interceptor.JmsEventErrorReporterInterceptorCodeGenerator;
import uk.gov.justice.subscription.jms.generator.interceptor.JmsLoggerMetadataInterceptorCodeGenerator;
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
                subscriptionMediaTypeToSchemaIdGenerator(),
                componentGeneratorTypesFactory());
    }

    public ServiceComponentGeneratorTypesFactory componentGeneratorTypesFactory() {
        return new ServiceComponentGeneratorTypesFactory(this);
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

    public EventInterceptorChainProviderCodeGenerator eventInterceptorChainProviderCodeGenerator() {
        return new EventInterceptorChainProviderCodeGenerator();
    }

    public JmsLoggerMetadataInterceptorCodeGenerator jmsLoggerMetadataInterceptorCodeGenerator() {
        return new JmsLoggerMetadataInterceptorCodeGenerator();
    }

    public JmsEventErrorReporterInterceptorCodeGenerator jmsEventErrorReporterInterceptorCodeGenerator() {
        return new JmsEventErrorReporterInterceptorCodeGenerator();
    }

    public SchemaIdParser schemaIdParser() {
        return new SchemaIdParser();
    }

    public JmsCommandHandlerDestinationNameProviderCodeGenerator jmsCommandHandlerDestinationNameProviderCodeGenerator() {
        return new JmsCommandHandlerDestinationNameProviderCodeGenerator();
    }

    public EventServiceComponentStrategy eventComponentStrategy(final ClassNameFactory classNameFactory, final Subscription subscription) {
        return new EventServiceComponentStrategy(classNameFactory, subscription);
    }
}
