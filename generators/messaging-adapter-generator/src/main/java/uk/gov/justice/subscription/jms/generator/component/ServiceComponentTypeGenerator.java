package uk.gov.justice.subscription.jms.generator.component;

import uk.gov.justice.services.generators.commons.config.CommonGeneratorProperties;
import uk.gov.justice.services.generators.subscription.parser.SubscriptionWrapper;
import uk.gov.justice.subscription.domain.subscriptiondescriptor.Subscription;

import java.util.stream.Stream;

import com.squareup.javapoet.TypeSpec;

public interface ServiceComponentTypeGenerator {

    Stream.Builder<TypeSpec> generatedClassesFrom(Stream.Builder<TypeSpec> streamBuilder,
                                                  final SubscriptionWrapper subscriptionWrapper,
                                                  final Subscription subscription,
                                                  final CommonGeneratorProperties commonGeneratorProperties);
}
