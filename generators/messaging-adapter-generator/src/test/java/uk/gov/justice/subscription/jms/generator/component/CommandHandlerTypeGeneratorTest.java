package uk.gov.justice.subscription.jms.generator.component;

import static com.squareup.javapoet.TypeSpec.classBuilder;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.justice.subscription.jms.generator.ClassNameFactory.JMS_HANDLER_DESTINATION_NAME_PROVIDER;

import uk.gov.justice.services.generators.commons.config.CommonGeneratorProperties;
import uk.gov.justice.services.generators.subscription.parser.SubscriptionWrapper;
import uk.gov.justice.subscription.domain.subscriptiondescriptor.Subscription;
import uk.gov.justice.subscription.jms.generator.ClassNameFactory;
import uk.gov.justice.subscription.jms.provider.JmsCommandHandlerDestinationNameProviderCodeGenerator;

import java.util.List;
import java.util.stream.Stream;

import com.squareup.javapoet.TypeSpec;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CommandHandlerTypeGeneratorTest {

    @Mock
    private DefaultTypeGenerator defaultComponentTypeGenerator;

    @Mock
    private JmsCommandHandlerDestinationNameProviderCodeGenerator jmsCommandHandlerDestinationNameProviderCodeGenerator;

    @Mock
    private ClassNameFactory classNameFactory;

    @InjectMocks
    private CommandHandlerTypeGenerator commandHandlerComponentTypeGenerator;

    @Test
    public void shouldAddGeneratedTypeSpecsToStream() {

        final Stream.Builder<TypeSpec> streamBuilder = Stream.builder();
        final SubscriptionWrapper subscriptionWrapper = mock(SubscriptionWrapper.class);
        final Subscription subscription = mock(Subscription.class);
        final CommonGeneratorProperties commonGeneratorProperties = mock(CommonGeneratorProperties.class);
        final TypeSpec jmsCommandHandlerDestinationNameProviderTypeSpec = classBuilder(JMS_HANDLER_DESTINATION_NAME_PROVIDER).build();

        when(jmsCommandHandlerDestinationNameProviderCodeGenerator.generate(subscriptionWrapper, subscription, classNameFactory)).thenReturn(jmsCommandHandlerDestinationNameProviderTypeSpec);
        when(defaultComponentTypeGenerator.generatedClassesFrom(streamBuilder, subscriptionWrapper, subscription, commonGeneratorProperties)).thenReturn(streamBuilder);

        final List<TypeSpec> typeSpecResult = commandHandlerComponentTypeGenerator.generatedClassesFrom(
                streamBuilder,
                subscriptionWrapper,
                subscription,
                commonGeneratorProperties)
                .build()
                .collect(toList());

        verify(defaultComponentTypeGenerator).generatedClassesFrom(streamBuilder, subscriptionWrapper, subscription, commonGeneratorProperties);

        assertThat(typeSpecResult.size(), is(1));
        assertThat(typeSpecResult.get(0), is(jmsCommandHandlerDestinationNameProviderTypeSpec));
    }
}