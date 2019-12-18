package uk.gov.justice.subscription.jms.generator.core;

import static uk.gov.justice.services.generators.commons.helper.Names.DEFAULT_ANNOTATION_PARAMETER;
import static uk.gov.justice.subscription.jms.generator.ClassNameFactory.JMS_LOGGER_METADATA_INTERCEPTOR;

import uk.gov.justice.services.adapter.messaging.JsonSchemaValidationInterceptor;
import uk.gov.justice.subscription.domain.subscriptiondescriptor.Subscription;
import uk.gov.justice.subscription.jms.generator.ClassNameFactory;

import java.util.Optional;

import javax.interceptor.Interceptors;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;

public class DefaultServiceComponentStrategy implements ServiceComponentStrategy {

    private static final String CLASS_NAME = "$T.class";

    private final ClassNameFactory classNameFactory;
    private final Subscription subscription;

    public DefaultServiceComponentStrategy(final ClassNameFactory classNameFactory, final Subscription subscription) {
        this.classNameFactory = classNameFactory;
        this.subscription = subscription;
    }

    @Override
    public Optional<AnnotationSpec> createAnnotationSpec() {

        if (!subscription.getEvents().isEmpty()) {

            return Optional.of(AnnotationSpec.builder(Interceptors.class)
                    .addMember(DEFAULT_ANNOTATION_PARAMETER, CLASS_NAME, classNameFactory.classNameFor(JMS_LOGGER_METADATA_INTERCEPTOR))
                    .addMember(DEFAULT_ANNOTATION_PARAMETER, CLASS_NAME, ClassName.get(JsonSchemaValidationInterceptor.class))
                    .build()
            );
        }

        return Optional.empty();
    }
}
