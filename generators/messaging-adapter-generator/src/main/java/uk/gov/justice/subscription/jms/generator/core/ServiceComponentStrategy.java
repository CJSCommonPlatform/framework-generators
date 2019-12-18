package uk.gov.justice.subscription.jms.generator.core;

import java.util.Optional;

import com.squareup.javapoet.AnnotationSpec;

public interface ServiceComponentStrategy {

    Optional<AnnotationSpec> createAnnotationSpec();
}
