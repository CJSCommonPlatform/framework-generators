package uk.gov.justice.subscription.jms.interceptor;

import static com.squareup.javapoet.ClassName.get;
import static com.squareup.javapoet.JavaFile.builder;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.test.utils.core.compiler.JavaCompilerUtility.javaCompilerUtil;
import static uk.gov.justice.subscription.jms.core.ClassNameFactory.JMS_LOGGER_METADATA_INTERCEPTOR;

import uk.gov.justice.services.adapter.messaging.JmsLoggerMetadataAdder;
import uk.gov.justice.services.generators.subscription.parser.SubscriptionWrapper;
import uk.gov.justice.subscription.domain.subscriptiondescriptor.SubscriptionsDescriptor;
import uk.gov.justice.subscription.jms.core.ClassNameFactory;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.interceptor.InvocationContext;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class JmsLoggerMetadataInterceptorCodeGeneratorTest {

    private static final File COMPILATION_OUTPUT_DIRECTORY = new File(System.getProperty("java.io.tmpdir"));
    private static final String SERVICE_COMPONENT_NAME = "CUSTOM";

    @InjectMocks
    private JmsLoggerMetadataInterceptorCodeGenerator jmsLoggerMetadataInterceptorCodeGenerator;

    @Test
    public void shouldGenerateJmsLoggerMetadataInterceptor() throws Exception {

        final String packageName = "uk.gov.justice.api.interceptor";
        final String simpleName = "MyCustomJmsLoggerMetadataInterceptorCodeGenerator";

        final ClassName jmsLoggerMetadataInterceptorClassName = get(packageName, simpleName);
        final ClassNameFactory classNameFactory = mock(ClassNameFactory.class);
        final SubscriptionWrapper subscriptionWrapper = mock(SubscriptionWrapper.class);
        final SubscriptionsDescriptor subscriptionsDescriptor = mock(SubscriptionsDescriptor.class);

        when(subscriptionWrapper.getSubscriptionsDescriptor()).thenReturn(subscriptionsDescriptor);
        when(subscriptionsDescriptor.getServiceComponent()).thenReturn(SERVICE_COMPONENT_NAME);
        when(classNameFactory.classNameFor(JMS_LOGGER_METADATA_INTERCEPTOR)).thenReturn(jmsLoggerMetadataInterceptorClassName);

        final TypeSpec typeSpec = jmsLoggerMetadataInterceptorCodeGenerator.generate(
                subscriptionWrapper,
                classNameFactory);

        final File outputDirectory = getOutputDirectory("./target/test-generation");
        builder(packageName, typeSpec)
                .build()
                .writeTo(outputDirectory);

        final Class<?> compiledClass = javaCompilerUtil().compiledClassOf(
                outputDirectory,
                COMPILATION_OUTPUT_DIRECTORY,
                packageName,
                simpleName);

        testGeneratedClass(compiledClass);
    }

    private void testGeneratedClass(final Class<?> compiledClass) throws Exception {

        final Object myCustomJmsLoggerMetadataInterceptor = compiledClass.newInstance();
        final JmsLoggerMetadataAdder jmsLoggerMetadataAdder = mock(JmsLoggerMetadataAdder.class);
        final InvocationContext invocationContext = mock(InvocationContext.class);
        final Object expected = mock(Object.class);

        final Field jmsLoggerMetadataAdderField = compiledClass.getDeclaredField("jmsLoggerMetadataAdder");
        jmsLoggerMetadataAdderField.setAccessible(true);
        jmsLoggerMetadataAdderField.set(myCustomJmsLoggerMetadataInterceptor, jmsLoggerMetadataAdder);

        when(jmsLoggerMetadataAdder.addRequestDataToMdc(invocationContext, SERVICE_COMPONENT_NAME)).thenReturn(expected);

        final Method addRequestDataToMappedDiagnosticContext = compiledClass.getMethod("addRequestDataToMdc", InvocationContext.class);

        final Object result = addRequestDataToMappedDiagnosticContext.invoke(myCustomJmsLoggerMetadataInterceptor, invocationContext);

        assertThat(result, is(expected));

        verify(jmsLoggerMetadataAdder).addRequestDataToMdc(invocationContext, SERVICE_COMPONENT_NAME);
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "SameParameterValue"})
    private File getOutputDirectory(final String path) {
        final File outputDirectory = new File(path);

        if (outputDirectory.exists()) {
            outputDirectory.delete();
        }

        outputDirectory.mkdirs();

        return outputDirectory;
    }
}