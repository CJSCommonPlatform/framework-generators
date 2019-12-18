package uk.gov.justice.subscription.jms.generator.core;

import static uk.gov.justice.services.generators.commons.helper.Names.DEFAULT_ANNOTATION_PARAMETER;
import static uk.gov.justice.subscription.jms.generator.ClassNameFactory.EVENT_VALIDATION_INTERCEPTOR;
import static uk.gov.justice.subscription.jms.generator.ClassNameFactory.JMS_EVENT_ERROR_REPORTER_INTERCEPTOR;
import static uk.gov.justice.subscription.jms.generator.ClassNameFactory.JMS_LOGGER_METADATA_INTERCEPTOR;

import uk.gov.justice.subscription.domain.subscriptiondescriptor.Subscription;
import uk.gov.justice.subscription.jms.generator.ClassNameFactory;

import java.util.Optional;

import javax.interceptor.Interceptors;

import com.squareup.javapoet.AnnotationSpec;

public class EventServiceComponentStrategy implements ServiceComponentStrategy {

    private static final String CLASS_NAME = "$T.class";

    private final ClassNameFactory classNameFactory;
    private final Subscription subscription;

    public EventServiceComponentStrategy(final ClassNameFactory classNameFactory,
                                         final Subscription subscription) {
        this.classNameFactory = classNameFactory;
        this.subscription = subscription;
    }

    @Override
    public Optional<AnnotationSpec> createAnnotationSpec() {

        if (!subscription.getEvents().isEmpty()) {
            return Optional.of(AnnotationSpec.builder(Interceptors.class)
                    .addMember(DEFAULT_ANNOTATION_PARAMETER, CLASS_NAME, classNameFactory.classNameFor(JMS_EVENT_ERROR_REPORTER_INTERCEPTOR))
                    .addMember(DEFAULT_ANNOTATION_PARAMETER, CLASS_NAME, classNameFactory.classNameFor(JMS_LOGGER_METADATA_INTERCEPTOR))
                    .addMember(DEFAULT_ANNOTATION_PARAMETER, CLASS_NAME, classNameFactory.classNameFor(EVENT_VALIDATION_INTERCEPTOR))
                    .build());

        }

        return Optional.empty();
    }
}
