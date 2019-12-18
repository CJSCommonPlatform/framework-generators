package uk.gov.justice.subscription.jms.generator.interceptor;

import static com.squareup.javapoet.ClassName.get;
import static com.squareup.javapoet.JavaFile.builder;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.test.utils.core.compiler.JavaCompilerUtility.javaCompilerUtil;
import static uk.gov.justice.subscription.jms.generator.ClassNameFactory.JMS_EVENT_ERROR_REPORTER_INTERCEPTOR;

import uk.gov.justice.services.adapter.messaging.JmsMessageProcessErrorReporter;
import uk.gov.justice.services.generators.subscription.parser.SubscriptionWrapper;
import uk.gov.justice.subscription.domain.subscriptiondescriptor.SubscriptionsDescriptor;
import uk.gov.justice.subscription.jms.generator.ClassNameFactory;

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
public class JmsEventErrorReporterInterceptorCodeGeneratorTest {

    private static final File COMPILATION_OUTPUT_DIRECTORY = new File(System.getProperty("java.io.tmpdir"));
    private static final String SERVICE_COMPONENT_NAME = "CUSTOM";

    @InjectMocks
    private JmsEventErrorReporterInterceptorCodeGenerator jmsEventErrorReporterInterceptorCodeGenerator;

    @Test
    public void shouldGenerateJmsEventErrorReporterInterceptor() throws Exception {

        final String packageName = "uk.gov.justice.api.interceptor";
        final String simpleName = "MyCustomJmsEventErrorReporterInterceptorCodeGenerator";

        final ClassName jmsLoggerMetadataInterceptorClassName = get(packageName, simpleName);
        final ClassNameFactory classNameFactory = mock(ClassNameFactory.class);
        final SubscriptionWrapper subscriptionWrapper = mock(SubscriptionWrapper.class);
        final SubscriptionsDescriptor subscriptionsDescriptor = mock(SubscriptionsDescriptor.class);

        when(subscriptionWrapper.getSubscriptionsDescriptor()).thenReturn(subscriptionsDescriptor);
        when(subscriptionsDescriptor.getServiceComponent()).thenReturn(SERVICE_COMPONENT_NAME);
        when(classNameFactory.classNameFor(JMS_EVENT_ERROR_REPORTER_INTERCEPTOR)).thenReturn(jmsLoggerMetadataInterceptorClassName);

        final TypeSpec typeSpec = jmsEventErrorReporterInterceptorCodeGenerator.generate(
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

        final Object myCustomJmsMessageProcessErrorReporter = compiledClass.newInstance();
        final JmsMessageProcessErrorReporter jmsMessageProcessErrorReporter = mock(JmsMessageProcessErrorReporter.class);
        final InvocationContext invocationContext = mock(InvocationContext.class);
        final Object expected = mock(Object.class);

        final Field jmsMessageProcessErrorReporterField = compiledClass.getDeclaredField("jmsMessageProcessErrorReporter");
        jmsMessageProcessErrorReporterField.setAccessible(true);
        jmsMessageProcessErrorReporterField.set(myCustomJmsMessageProcessErrorReporter, jmsMessageProcessErrorReporter);

        when(jmsMessageProcessErrorReporter.catchAndReportError(invocationContext, SERVICE_COMPONENT_NAME)).thenReturn(expected);

        final Method addRequestDataToMappedDiagnosticContext = compiledClass.getMethod("catchAndReportError", InvocationContext.class);

        final Object result = addRequestDataToMappedDiagnosticContext.invoke(myCustomJmsMessageProcessErrorReporter, invocationContext);

        assertThat(result, is(expected));

        verify(jmsMessageProcessErrorReporter).catchAndReportError(invocationContext, SERVICE_COMPONENT_NAME);
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