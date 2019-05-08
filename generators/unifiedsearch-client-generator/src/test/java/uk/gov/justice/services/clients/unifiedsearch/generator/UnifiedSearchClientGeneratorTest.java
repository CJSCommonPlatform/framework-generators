package uk.gov.justice.services.clients.unifiedsearch.generator;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static uk.gov.justice.config.GeneratorPropertiesFactory.generatorProperties;
import static uk.gov.justice.services.generators.test.utils.config.GeneratorConfigUtil.configurationWithBasePackage;
import static uk.gov.justice.services.test.utils.core.compiler.JavaCompilerUtility.javaCompilerUtil;
import static uk.gov.justice.services.test.utils.core.reflection.ReflectionUtil.methodsOf;

import uk.gov.justice.maven.generator.io.files.parser.core.GeneratorProperties;
import uk.gov.justice.services.clients.unifiedsearch.generator.domain.Event;
import uk.gov.justice.services.clients.unifiedsearch.generator.domain.UnifiedSearchDescriptor;
import uk.gov.justice.services.core.annotation.Handles;
import uk.gov.justice.services.core.annotation.ServiceComponent;
import uk.gov.justice.services.test.utils.core.compiler.JavaCompilerUtility;
import uk.gov.justice.services.unifiedsearch.UnifiedSearchIndexer;
import uk.gov.justice.services.unifiedsearch.UnifiedSearchName;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;

public class UnifiedSearchClientGeneratorTest {

    private static final String BASE_PACKAGE = "uk.gov.justice.services.clients.unifiedsearch.generator";
    private static final JavaCompilerUtility COMPILER = javaCompilerUtil();
    private static final GeneratorProperties EVENT_INDEXER = generatorProperties().withServiceComponentOf("EVENT_INDEXER");

    @Rule
    public TemporaryFolder outputFolder = new TemporaryFolder();


    @Test
    public void shouldGenerateClassWithServiceComponentAnnotations() throws Exception {

        final Class<?> generatedClass = compiledGeneratedClass(unifiedSearchDescriptor());
        assertThat(generatedClass.getCanonicalName(), is(BASE_PACKAGE + ".UnifiedSearchEventIndexer"));
        assertThat(generatedClass.getAnnotation(ServiceComponent.class), not(nullValue()));
        assertThat(generatedClass.getAnnotation(ServiceComponent.class).value(), is("EVENT_INDEXER"));
    }

    @Test
    public void shouldGenerateClassWithOneHandlesMethodWithHandlesAnnotations() throws Exception {

        final Class<?> generatedClass = compiledGeneratedClass(unifiedSearchDescriptor());

        final List<Method> methods = methodsOf(generatedClass);
        assertThat(methods, hasSize(1));

        final Method method = methods.get(0);
        final Handles handlesAnnotation = method.getAnnotation(Handles.class);
        assertThat(handlesAnnotation, not(nullValue()));
        assertThat(handlesAnnotation.value(), is("example.recipe-added"));
    }


    @Test
    public void shouldGenerateClassWithFieldUnifiedSearchIndexer() throws Exception {
        final Class<?> generatedClass = compiledGeneratedClass(unifiedSearchDescriptor());

        final Field unifiedSearchIndexer = generatedClass.getDeclaredField("unifiedSearchIndexer");
        assertThat(unifiedSearchIndexer, not(nullValue()));
        assertThat(unifiedSearchIndexer.getType(), equalTo(UnifiedSearchIndexer.class));
        final Annotation[] annotations = unifiedSearchIndexer.getAnnotations();

        final Annotation annotationInject = annotations[0];
        assertThat(annotationInject.annotationType().getName(), equalTo("javax.inject.Inject"));

        final Annotation annotationUnifiedSearchName = annotations[1];
        assertThat(annotationUnifiedSearchName.annotationType(), equalTo(UnifiedSearchName.class));
        assertThat(((UnifiedSearchName) annotationUnifiedSearchName).value(), is("case_details"));

    }

    @Test
    public void shouldGenerateClassWithFieldUnifiedSearchAdapter() throws Exception {
        final Class<?> generatedClass = compiledGeneratedClass(unifiedSearchDescriptor());

        final Field unifiedSearchAdapter = generatedClass.getDeclaredField("unifiedSearchAdapter");
        assertThat(unifiedSearchAdapter, not(nullValue()));
        assertThat(unifiedSearchAdapter.getType(), equalTo(UnifiedSearchAdapter.class));
    }

    @Test
    public void shouldCreateLoggerConstant() throws Exception {

        final Class<?> generatedClass = compiledGeneratedClass(unifiedSearchDescriptor());
        final Field logger = generatedClass.getDeclaredField("LOGGER");
        assertThat(logger, not(nullValue()));
        assertThat(logger.getType(), equalTo(Logger.class));
        assertThat(Modifier.isPrivate(logger.getModifiers()), Matchers.is(true));
        assertThat(Modifier.isStatic(logger.getModifiers()), Matchers.is(true));
        assertThat(Modifier.isFinal(logger.getModifiers()), Matchers.is(true));
    }

    private Class<?> compiledGeneratedClass(final UnifiedSearchDescriptor unifiedSearchDescriptor) throws MalformedURLException {
        final UnifiedSearchClientGenerator generator = new UnifiedSearchClientGenerator();
        generator.run(unifiedSearchDescriptor, configurationWithBasePackage(BASE_PACKAGE, outputFolder, EVENT_INDEXER));
        return COMPILER.compiledClassOf(outputFolder.getRoot(), outputFolder.getRoot(), BASE_PACKAGE, "UnifiedSearchEventIndexer");
    }

    private UnifiedSearchDescriptor unifiedSearchDescriptor() {
        final List<Event> events = new ArrayList<>();
        final Event event = new Event("example.recipe-added", "/json/sample/prosecutioncase-to-case-spec.json", "case_details");
        events.add(event);
        return new UnifiedSearchDescriptor("example", "1.0.0", "examplecontext", "EVENT_INDEXER", events);
    }

    private UnifiedSearchDescriptor incorrectunifiedSearchDescriptor() {
        final List<Event> events = new ArrayList<>();
        final Event event = new Event("example.recipe-added", "/json/sample/prosecutioncase-to-case-spec.json", "case_details");
        events.add(event);
        return new UnifiedSearchDescriptor("example", "1.0.0", "examplecontext", null, events);
    }

}