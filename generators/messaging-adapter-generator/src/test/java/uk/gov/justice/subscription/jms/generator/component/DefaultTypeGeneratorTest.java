package uk.gov.justice.subscription.jms.generator.component;

import static com.squareup.javapoet.TypeSpec.classBuilder;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import uk.gov.justice.services.generators.commons.config.CommonGeneratorProperties;
import uk.gov.justice.services.generators.subscription.parser.SubscriptionWrapper;
import uk.gov.justice.subscription.domain.subscriptiondescriptor.Subscription;
import uk.gov.justice.subscription.jms.generator.ClassNameFactory;
import uk.gov.justice.subscription.jms.generator.core.MessageListenerCodeGenerator;
import uk.gov.justice.subscription.jms.generator.core.ServiceComponentStrategy;
import uk.gov.justice.subscription.jms.generator.interceptor.JmsLoggerMetadataInterceptorCodeGenerator;

import java.util.List;
import java.util.stream.Stream;

import com.squareup.javapoet.TypeSpec;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DefaultTypeGeneratorTest {

    @Mock
    private MessageListenerCodeGenerator messageListenerCodeGenerator;

    @Mock
    private JmsLoggerMetadataInterceptorCodeGenerator jmsLoggerMetadataInterceptorCodeGenerator;

    @Mock
    private ClassNameFactory classNameFactory;

    @Mock
    private ServiceComponentStrategy serviceComponentStrategy;

    @InjectMocks
    private DefaultTypeGenerator defaultComponentTypeGenerator;


    @Test
    public void shouldAddGeneratedTypeSpecsToStream() {

        final Stream.Builder<TypeSpec> streamBuilder = Stream.builder();
        final SubscriptionWrapper subscriptionWrapper = mock(SubscriptionWrapper.class);
        final Subscription subscription = mock(Subscription.class);
        final CommonGeneratorProperties commonGeneratorProperties = mock(CommonGeneratorProperties.class);
        final TypeSpec jmsLoggerMetadataInterceptorTypeSpec = classBuilder("jmsLoggerMetadataAdder").build();
        final TypeSpec messageListenerTypeSpec = classBuilder("JmsListener").build();

        when(jmsLoggerMetadataInterceptorCodeGenerator.generate(subscriptionWrapper, classNameFactory)).thenReturn(jmsLoggerMetadataInterceptorTypeSpec);
        when(messageListenerCodeGenerator.generate(subscriptionWrapper, subscription, commonGeneratorProperties, classNameFactory, serviceComponentStrategy)).thenReturn(messageListenerTypeSpec);

        final List<TypeSpec> typeSpecResult = defaultComponentTypeGenerator.generatedClassesFrom(
                streamBuilder,
                subscriptionWrapper,
                subscription,
                commonGeneratorProperties)
                .build()
                .collect(toList());

        assertThat(typeSpecResult.size(), is(2));
        assertThat(typeSpecResult.get(0), is(jmsLoggerMetadataInterceptorTypeSpec));
        assertThat(typeSpecResult.get(1), is(messageListenerTypeSpec));
    }
}