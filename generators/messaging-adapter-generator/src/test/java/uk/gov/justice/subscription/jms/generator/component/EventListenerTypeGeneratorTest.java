package uk.gov.justice.subscription.jms.generator.component;

import static com.squareup.javapoet.TypeSpec.classBuilder;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.core.annotation.Component.EVENT_LISTENER;
import static uk.gov.justice.subscription.jms.generator.ClassNameFactory.EVENT_FILTER;
import static uk.gov.justice.subscription.jms.generator.ClassNameFactory.EVENT_FILTER_INTERCEPTOR;
import static uk.gov.justice.subscription.jms.generator.ClassNameFactory.EVENT_INTERCEPTOR_CHAIN_PROVIDER;
import static uk.gov.justice.subscription.jms.generator.ClassNameFactory.EVENT_VALIDATION_INTERCEPTOR;
import static uk.gov.justice.subscription.jms.generator.ClassNameFactory.JMS_EVENT_ERROR_REPORTER_INTERCEPTOR;

import uk.gov.justice.services.generators.commons.config.CommonGeneratorProperties;
import uk.gov.justice.services.generators.subscription.parser.SubscriptionWrapper;
import uk.gov.justice.subscription.domain.subscriptiondescriptor.Subscription;
import uk.gov.justice.subscription.jms.generator.ClassNameFactory;
import uk.gov.justice.subscription.jms.generator.framework.EventFilterCodeGenerator;
import uk.gov.justice.subscription.jms.generator.framework.EventFilterInterceptorCodeGenerator;
import uk.gov.justice.subscription.jms.generator.framework.EventInterceptorChainProviderCodeGenerator;
import uk.gov.justice.subscription.jms.generator.framework.EventValidationInterceptorCodeGenerator;
import uk.gov.justice.subscription.jms.generator.interceptor.JmsEventErrorReporterInterceptorCodeGenerator;

import java.util.List;
import java.util.stream.Stream;

import com.squareup.javapoet.TypeSpec;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EventListenerTypeGeneratorTest {

    @Mock
    private DefaultTypeGenerator defaultComponentTypeGenerator;

    @Mock
    private EventFilterCodeGenerator eventFilterCodeGenerator;

    @Mock
    private EventFilterInterceptorCodeGenerator eventFilterInterceptorCodeGenerator;

    @Mock
    private EventValidationInterceptorCodeGenerator eventValidationInterceptorCodeGenerator;

    @Mock
    private EventInterceptorChainProviderCodeGenerator eventInterceptorChainProviderCodeGenerator;

    @Mock
    private JmsEventErrorReporterInterceptorCodeGenerator jmsEventErrorReporterInterceptorCodeGenerator;

    @Mock
    private ClassNameFactory classNameFactory;

    @InjectMocks
    private EventListenerTypeGenerator eventComponentTypeGenerator;

    @Test
    public void shouldAddGeneratedTypeSpecsToStream() {

        final Stream.Builder<TypeSpec> streamBuilder = Stream.builder();
        final SubscriptionWrapper subscriptionWrapper = mock(SubscriptionWrapper.class);
        final Subscription subscription = mock(Subscription.class);
        final CommonGeneratorProperties commonGeneratorProperties = mock(CommonGeneratorProperties.class);
        final TypeSpec eventFilterTypeSpec = classBuilder(EVENT_FILTER).build();
        final TypeSpec eventFilterInterceptorTypeSpec = classBuilder(EVENT_FILTER_INTERCEPTOR).build();
        final TypeSpec eventValidationInterceptorTypeSpec = classBuilder(EVENT_VALIDATION_INTERCEPTOR).build();
        final TypeSpec eventInterceptorChainProviderTypeSpec = classBuilder(EVENT_INTERCEPTOR_CHAIN_PROVIDER).build();
        final TypeSpec jmsEventErrorReporterInterceptorTypeSpec = classBuilder(JMS_EVENT_ERROR_REPORTER_INTERCEPTOR).build();

        when(commonGeneratorProperties.getServiceComponent()).thenReturn(EVENT_LISTENER);
        when(eventFilterCodeGenerator.generate(subscription, classNameFactory)).thenReturn(eventFilterTypeSpec);
        when(eventFilterInterceptorCodeGenerator.generate(classNameFactory)).thenReturn(eventFilterInterceptorTypeSpec);
        when(eventValidationInterceptorCodeGenerator.generate(classNameFactory)).thenReturn(eventValidationInterceptorTypeSpec);
        when(eventInterceptorChainProviderCodeGenerator.generate(EVENT_LISTENER, classNameFactory)).thenReturn(eventInterceptorChainProviderTypeSpec);
        when(jmsEventErrorReporterInterceptorCodeGenerator.generate(subscriptionWrapper, classNameFactory)).thenReturn(jmsEventErrorReporterInterceptorTypeSpec);

        when(defaultComponentTypeGenerator.generatedClassesFrom(streamBuilder, subscriptionWrapper, subscription, commonGeneratorProperties)).thenReturn(streamBuilder);

        final List<TypeSpec> typeSpecResult = eventComponentTypeGenerator.generatedClassesFrom(
                streamBuilder,
                subscriptionWrapper,
                subscription,
                commonGeneratorProperties)
                .build()
                .collect(toList());

        verify(defaultComponentTypeGenerator).generatedClassesFrom(streamBuilder, subscriptionWrapper, subscription, commonGeneratorProperties);

        assertThat(typeSpecResult.size(), is(5));
        assertThat(typeSpecResult.get(0), is(eventFilterTypeSpec));
        assertThat(typeSpecResult.get(1), is(eventFilterInterceptorTypeSpec));
        assertThat(typeSpecResult.get(2), is(eventValidationInterceptorTypeSpec));
        assertThat(typeSpecResult.get(3), is(eventInterceptorChainProviderTypeSpec));
        assertThat(typeSpecResult.get(4), is(jmsEventErrorReporterInterceptorTypeSpec));
    }
}