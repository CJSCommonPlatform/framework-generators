package uk.gov.justice.services.clients.unifiedsearch.generator;

import static java.lang.reflect.Modifier.*;
import static java.util.Arrays.asList;
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
import uk.gov.justice.services.clients.unifiedsearch.core.domain.Event;
import uk.gov.justice.services.clients.unifiedsearch.core.domain.UnifiedSearchDescriptor;
import uk.gov.justice.services.core.annotation.Handles;
import uk.gov.justice.services.core.annotation.ServiceComponent;
import uk.gov.justice.services.test.utils.core.compiler.JavaCompilerUtility;
import uk.gov.justice.services.unifiedsearch.UnifiedSearchIndexer;
import uk.gov.justice.services.unifiedsearch.UnifiedSearchName;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;

public class UnifiedSearchClientGeneratorTest {

    private static final String BASE_PACKAGE = "uk.gov.justice.services.clients.unifiedsearch.generator";
    private static final JavaCompilerUtility COMPILER = javaCompilerUtil();
    private static final GeneratorProperties EVENT_INDEXER = generatorProperties().withServiceComponentOf("EVENT_INDEXER");

    private static final String FIRST_EVENT_INDEXER = "ExamplecontextEventIndexer1";
    private static final String SECOND_EVENT_INDEXER = "ExamplecontextEventIndexer2";

    private  List<Class<?>> classList;

    @Before
    public void setUp() throws MalformedURLException {
        final UnifiedSearchDescriptor unifiedSearchDescriptor = unifiedSearchDescriptor();
         classList = compiledGeneratedClass(unifiedSearchDescriptor ,asList(FIRST_EVENT_INDEXER, SECOND_EVENT_INDEXER));
    }

    @Rule
    public TemporaryFolder outputFolder = new TemporaryFolder();

    @Test
    public void shouldGenerateClassesWithServiceComponentAnnotations() throws Exception {

        assertThat(classList.get(0).getCanonicalName(), is(BASE_PACKAGE + ".ExamplecontextEventIndexer1"));
        assertThat(classList.get(0).getAnnotation(ServiceComponent.class), not(nullValue()));
        assertThat(classList.get(0).getAnnotation(ServiceComponent.class).value(), is("EVENT_INDEXER"));

        assertThat(classList.get(1).getCanonicalName(), is(BASE_PACKAGE + ".ExamplecontextEventIndexer2"));
        assertThat(classList.get(1).getAnnotation(ServiceComponent.class), not(nullValue()));
        assertThat(classList.get(1).getAnnotation(ServiceComponent.class).value(), is("EVENT_INDEXER"));
    }

    @Test
    public void shouldGenerateClassWithOneHandlesMethodWithHandlesAnnotations() throws Exception {

        final List<Method> methods1 = methodsOf(classList.get(0));
        final List<Method> methods2 = methodsOf(classList.get(1));

        final Method method1 = methods1.get(0);
        final Method method2 = methods2.get(0);

        final Handles handlesAnnotation1 = method1.getAnnotation(Handles.class);
        final Handles handlesAnnotation2 = method2.getAnnotation(Handles.class);

        assertThat(methods1, hasSize(1));
        assertThat(methods2, hasSize(1));
        assertThat(handlesAnnotation1, not(nullValue()));
        assertThat(handlesAnnotation1.value(), is("example.recipe-added"));
        assertThat(handlesAnnotation2, not(nullValue()));
        assertThat(handlesAnnotation2.value(), is("example.recipe-removed"));
    }


    @Test
    public void shouldGenerateClassesWithFieldUnifiedSearchIndexer() throws Exception {

        final Field unifiedSearchIndexer1 = classList.get(0).getDeclaredField("unifiedSearchIndexer");
        final Field unifiedSearchIndexer2 = classList.get(1).getDeclaredField("unifiedSearchIndexer");

        final Annotation[] annotations1 = unifiedSearchIndexer1.getAnnotations();
        final Annotation[] annotations2 = unifiedSearchIndexer2.getAnnotations();

        final Annotation annotationInject1 = annotations1[0];
        final Annotation annotationInject2 = annotations1[0];

        final Annotation annotationUnifiedSearchName1 = annotations1[1];
        final Annotation annotationUnifiedSearchName2 = annotations2[1];

        assertThat(unifiedSearchIndexer1, not(nullValue()));
        assertThat(unifiedSearchIndexer1.getType(), equalTo(UnifiedSearchIndexer.class));
        assertThat(annotationInject1.annotationType().getName(), equalTo("javax.inject.Inject"));
        assertThat(annotationUnifiedSearchName1.annotationType(), equalTo(UnifiedSearchName.class));
        assertThat(((UnifiedSearchName) annotationUnifiedSearchName1).value(), is("recipe_details1"));

        assertThat(unifiedSearchIndexer2, not(nullValue()));
        assertThat(unifiedSearchIndexer2.getType(), equalTo(UnifiedSearchIndexer.class));
        assertThat(annotationInject2.annotationType().getName(), equalTo("javax.inject.Inject"));
        assertThat(annotationUnifiedSearchName2.annotationType(), equalTo(UnifiedSearchName.class));
        assertThat(((UnifiedSearchName) annotationUnifiedSearchName2).value(), is("recipe_details2"));

    }

    @Test
    public void shouldGenerateClassesWithFieldUnifiedSearchAdapter() throws Exception {

        final Field unifiedSearchAdapter1 = classList.get(0).getDeclaredField("unifiedSearchAdapter");
        final Field unifiedSearchAdapter2 = classList.get(1).getDeclaredField("unifiedSearchAdapter");

        assertThat(unifiedSearchAdapter1, not(nullValue()));
        assertThat(unifiedSearchAdapter1.getType(), equalTo(UnifiedSearchAdapter.class));

        assertThat(unifiedSearchAdapter2, not(nullValue()));
        assertThat(unifiedSearchAdapter2.getType(), equalTo(UnifiedSearchAdapter.class));
    }

    @Test
    public void shouldCreateLoggerConstant() throws Exception {

        final Field logger1 = classList.get(0).getDeclaredField("LOGGER");
        final Field logger2 = classList.get(1).getDeclaredField("LOGGER");

        assertThat(logger1, not(nullValue()));
        assertThat(logger1.getType(), equalTo(Logger.class));
        assertThat(isPrivate(logger1.getModifiers()), Matchers.is(true));
        assertThat(isStatic(logger1.getModifiers()), Matchers.is(true));
        assertThat(isFinal(logger1.getModifiers()), Matchers.is(true));

        assertThat(logger2, not(nullValue()));
        assertThat(logger2.getType(), equalTo(Logger.class));
        assertThat(isPrivate(logger2.getModifiers()), Matchers.is(true));
        assertThat(isStatic(logger2.getModifiers()), Matchers.is(true));
        assertThat(isFinal(logger2.getModifiers()), Matchers.is(true));
    }

    private List<Class<?>> compiledGeneratedClass(final UnifiedSearchDescriptor unifiedSearchDescriptor, final List<String> classNames) throws MalformedURLException {
        final UnifiedSearchClientGenerator generator = new UnifiedSearchClientGenerator();
        final List<Class<?>> classes = new ArrayList<>();

        generator.run(unifiedSearchDescriptor, configurationWithBasePackage(BASE_PACKAGE, outputFolder, EVENT_INDEXER));

        classNames.forEach(className -> classes.add(COMPILER.compiledClassOf(outputFolder.getRoot(), outputFolder.getRoot(), BASE_PACKAGE, className)));
        return classes;
    }

    private UnifiedSearchDescriptor unifiedSearchDescriptor() {
        final Event event1 = new Event("example.recipe-added", "/json/sample/recipe-added-spec.json", "recipe_details1");
        final Event event2 = new Event("example.recipe-removed", "/json/sample/recipe-removed-spec.json", "recipe_details2");
        return new UnifiedSearchDescriptor("example", "1.0.0", "examplecontext", "EVENT_INDEXER", asList(event1, event2));
    }

}