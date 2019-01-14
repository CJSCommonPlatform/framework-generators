package uk.gov.justice.services.adapters.rest.generator;

import static com.squareup.javapoet.JavaFile.builder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.test.utils.core.compiler.JavaCompilerUtility.javaCompilerUtil;

import uk.gov.justice.maven.generator.io.files.parser.core.GeneratorConfig;
import uk.gov.justice.services.adapter.rest.filter.LoggerRequestDataAdder;
import uk.gov.justice.services.generators.commons.config.CommonGeneratorProperties;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;

import com.squareup.javapoet.TypeSpec;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.raml.model.Raml;

@RunWith(MockitoJUnitRunner.class)
public class LoggerRequestDataFilterGeneratorTest {

    private static final File COMPILATION_OUTPUT_DIRECTORY = new File(System.getProperty("java.io.tmpdir"));
    private static final String SERVICE_COMPONENT_NAME = "CUSTOM_API";

    @InjectMocks
    private LoggerRequestDataFilterGenerator loggerRequestDataFilterGenerator;

    @Test
    public void shouldGenerateJmsLoggerMetadataInterceptor() throws Exception {

        final String packageName = "uk.gov.justice.api.filter";
        final String simpleName = "CustomApiRestExampleLoggerRequestDataFilter";
        final String baseUri = "http://localhost:8080/rest-adapter-generator/custom/api/rest/example";

        final CommonGeneratorProperties commonGeneratorProperties = mock(CommonGeneratorProperties.class);

        final Raml raml = mock(Raml.class);
        final GeneratorConfig generatorConfig = mock(GeneratorConfig.class);

        when(generatorConfig.getGeneratorProperties()).thenReturn(commonGeneratorProperties);
        when(commonGeneratorProperties.getServiceComponent()).thenReturn(SERVICE_COMPONENT_NAME);
        when(raml.getBaseUri()).thenReturn(baseUri);

        final List<TypeSpec> typeSpec = loggerRequestDataFilterGenerator.generateFor(
                raml,
                generatorConfig);

        final File outputDirectory = getOutputDirectory("./target/test-generation");
        builder(packageName, typeSpec.get(0))
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

        final Object customFilter = compiledClass.newInstance();
        final LoggerRequestDataAdder loggerRequestDataAdder = mock(LoggerRequestDataAdder.class);
        final ContainerRequestContext containerRequestContext = mock(ContainerRequestContext.class);
        final ContainerResponseContext containerResponseContext = mock(ContainerResponseContext.class);

        final Field loggerRequestDataAdderField = compiledClass.getDeclaredField("loggerRequestDataAdder");
        loggerRequestDataAdderField.setAccessible(true);
        loggerRequestDataAdderField.set(customFilter, loggerRequestDataAdder);

        final Method filterRequest = compiledClass.getMethod("filter", ContainerRequestContext.class);

        filterRequest.invoke(customFilter, containerRequestContext);

        verify(loggerRequestDataAdder).addToMdc(containerRequestContext, SERVICE_COMPONENT_NAME);

        final Method filterResponse = compiledClass.getMethod("filter", ContainerRequestContext.class, ContainerResponseContext.class);

        filterResponse.invoke(customFilter, containerRequestContext, containerResponseContext);

        verify(loggerRequestDataAdder).clearMdc();
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