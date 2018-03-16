package uk.gov.justice.services.clients.rest.generator;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.raml.model.ActionType.DELETE;
import static org.raml.model.ActionType.GET;
import static org.raml.model.ActionType.PATCH;
import static org.raml.model.ActionType.POST;
import static org.raml.model.ActionType.PUT;
import static uk.gov.justice.config.GeneratorPropertiesFactory.generatorProperties;
import static uk.gov.justice.services.generators.test.utils.builder.HttpActionBuilder.httpAction;
import static uk.gov.justice.services.generators.test.utils.builder.MappingBuilder.mapping;
import static uk.gov.justice.services.generators.test.utils.builder.MappingDescriptionBuilder.mappingDescriptionWith;
import static uk.gov.justice.services.generators.test.utils.builder.QueryParamBuilder.queryParam;
import static uk.gov.justice.services.generators.test.utils.builder.RamlBuilder.raml;
import static uk.gov.justice.services.generators.test.utils.builder.RamlBuilder.restRamlWithDefaults;
import static uk.gov.justice.services.generators.test.utils.builder.RamlBuilder.restRamlWithQueryApiDefaults;
import static uk.gov.justice.services.generators.test.utils.builder.RamlBuilder.restRamlWithTitleVersion;
import static uk.gov.justice.services.generators.test.utils.builder.ResourceBuilder.resource;
import static uk.gov.justice.services.generators.test.utils.config.GeneratorConfigUtil.configurationWithBasePackage;
import static uk.gov.justice.services.test.utils.core.reflection.ReflectionUtil.firstMethodOf;
import static uk.gov.justice.services.test.utils.core.reflection.ReflectionUtil.methodsOf;

import uk.gov.justice.services.core.annotation.FrameworkComponent;
import uk.gov.justice.services.core.annotation.Handles;
import uk.gov.justice.services.core.annotation.Remote;
import uk.gov.justice.services.generators.commons.config.CommonGeneratorProperties;
import uk.gov.justice.services.generators.test.utils.BaseGeneratorTest;
import uk.gov.justice.services.messaging.JsonEnvelope;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.util.List;

import javax.inject.Inject;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

@RunWith(MockitoJUnitRunner.class)
public class RestClientGenerator_CodeStructureTest extends BaseGeneratorTest {

    private static final String GET_MAPPING_ANNOTATION = mappingDescriptionWith(
            mapping()
                    .withResponseType("application/vnd.cakeshop.query.recipe+json")
                    .withName("cakeshop.get-recipe"))
            .build();

    private static final String POST_MAPPING_ANNOTATION = mappingDescriptionWith(
            mapping()
                    .withRequestType("application/vnd.cakeshop.command.create-recipe+json")
                    .withName("cakeshop.create-recipe"))
            .build();

    private static final String PUT_MAPPING_ANNOTATION = mappingDescriptionWith(
            mapping()
                    .withRequestType("application/vnd.cakeshop.command.update-recipe+json")
                    .withName("cakeshop.update-recipe"))
            .build();

    private static final String PATCH_MAPPING_ANNOTATION = mappingDescriptionWith(
            mapping()
                    .withRequestType("application/vnd.cakeshop.command.patch-recipe+json")
                    .withName("cakeshop.patch-recipe"))
            .build();

    private static final String DELETE_MAPPING_ANNOTATION = mappingDescriptionWith(
            mapping()
                    .withRequestType("application/vnd.cakeshop.command.delete-recipe+json")
                    .withName("cakeshop.delete-recipe"))
            .build();

    private static final String BASE_PACKAGE = "org.raml.test";
    private static final String BASE_URI_WITH_LESS_THAN_EIGHT_PARTS = "http://localhost:8080/command/api/rest/service";
    private static final String BASE_URI_WITH_MORE_THAN_EIGHT_PARTS = "http://localhost:8080/warname/command/api/rest/service/extra";
    private static final String BASE_URI_WITH_HYPHENATED_SERVICE_NAME = "http://localhost:8080/warname/command/api/rest/service-with-hyphens";

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void before() {
        super.before();
        generator = new RestClientGenerator();
    }

    @Test
    public void shouldGenerateClassWithAnnotations() throws Exception {
        generator.run(
                raml()
                        .withBaseUri("http://localhost:8080/warname/query/api/rest/service")
                        .with(resource("/some/path/{recipeId}")
                                .with(httpAction(GET, "application/vnd.cakeshop.query.add-recipe+json")
                                        .with(queryParam("recipename").required(true), queryParam("topingredient").required(false)))
                        ).build(),
                configurationWithBasePackage(BASE_PACKAGE, outputFolder, generatorProperties().withServiceComponentOf("CUSTOM_COMPONENT")));


        final Class<?> generatedClass = compiler.compiledClassOf(BASE_PACKAGE, "RemoteCustomComponent2ServiceQueryApi");

        assertThat(generatedClass.getCanonicalName(), is("org.raml.test.RemoteCustomComponent2ServiceQueryApi"));
        assertThat(generatedClass.getAnnotation(Remote.class), not(nullValue()));
        assertThat(generatedClass.getAnnotation(FrameworkComponent.class), not(nullValue()));
        assertThat(generatedClass.getAnnotation(FrameworkComponent.class).value(), is("CUSTOM_COMPONENT"));
        assertBaseUriField(generatedClass.getDeclaredField("BASE_URI"));
        assertRestClientField(generatedClass.getDeclaredField("restClientProcessor"));
        assertRestClientHelperField(generatedClass.getDeclaredField("restClientHelper"));
        assertEnveloperField(generatedClass.getDeclaredField("enveloper"));
    }

    @Test
    public void shouldGenerateClassWithQueryControllerAnnotation() throws Exception {
        generator.run(
                raml()
                        .withBaseUri("http://localhost:8080/warname/query/api/rest/service")
                        .withDefaultPostResource()
                        .build(),
                configurationWithBasePackage(BASE_PACKAGE, outputFolder, generatorProperties().withServiceComponentOf("QUERY_CONTROLLER")));

        final Class<?> generatedClass = compiler.compiledClassOf(BASE_PACKAGE, "RemoteQueryController2ServiceQueryApi");
        assertThat(generatedClass.getAnnotation(FrameworkComponent.class).value(), is("QUERY_CONTROLLER"));
    }

    @Test
    public void shouldCreateLoggerConstant() throws Exception {
        generator.run(
                raml()
                        .withBaseUri("http://localhost:8080/warname/query/api/rest/service")
                        .withDefaultPostResource()
                        .build(),
                configurationWithBasePackage(BASE_PACKAGE, outputFolder, generatorProperties().withServiceComponentOf("CUSTOM_COMPONENT")));

        final Class<?> generatedClass = compiler.compiledClassOf(BASE_PACKAGE, "RemoteCustomComponent2ServiceQueryApi");

        final Field logger = generatedClass.getDeclaredField("LOGGER");
        assertThat(logger, not(nullValue()));
        assertThat(logger.getType(), equalTo(Logger.class));
        assertThat(Modifier.isPrivate(logger.getModifiers()), Matchers.is(true));
        assertThat(Modifier.isStatic(logger.getModifiers()), Matchers.is(true));
        assertThat(Modifier.isFinal(logger.getModifiers()), Matchers.is(true));
    }

    @Test
    public void shouldGenerateMethodAnnotatedWithHandlesAnnotationForGET() throws Exception {
        generator.run(
                restRamlWithDefaults()
                        .with(resource("/some/path/{recipeId}")
                                .with(httpAction(GET)
                                        .withResponseTypes("application/vnd.cakeshop.query.recipe+json")
                                        .withDescription(GET_MAPPING_ANNOTATION))
                        ).build(),
                configurationWithBasePackage(BASE_PACKAGE, outputFolder, generatorProperties().withServiceComponentOf("CUSTOM_COMPONENT")));

        final Class<?> clazz = compiler.compiledClassOf(BASE_PACKAGE, "RemoteCustomComponent2ServiceCommandApi");
        final List<Method> methods = methodsOf(clazz);
        assertThat(methods, hasSize(1));

        final Method method = methods.get(0);
        assertThat(method.getName(), equalTo("getSomePathRecipeIdCakeshopQueryRecipe"));
        final Handles handlesAnnotation = method.getAnnotation(Handles.class);
        assertThat(handlesAnnotation, not(nullValue()));
        assertThat(handlesAnnotation.value(), is("cakeshop.get-recipe"));

    }

    @Test
    public void shouldGenerateMethodAnnotatedWithHandlesAnnotationForPOST() throws Exception {
        generator.run(
                restRamlWithDefaults()
                        .with(resource("/some/path/{recipeId}")
                                .with(httpAction(POST, "application/vnd.cakeshop.command.create-recipe+json")
                                        .withDescription(POST_MAPPING_ANNOTATION))
                        ).build(),
                configurationWithBasePackage(BASE_PACKAGE, outputFolder, generatorProperties().withServiceComponentOf("CUSTOM_COMPONENT")));

        final Class<?> clazz = compiler.compiledClassOf(BASE_PACKAGE, "RemoteCustomComponent2ServiceCommandApi");
        final List<Method> methods = methodsOf(clazz);
        assertThat(methods, hasSize(1));

        final Method method = methods.get(0);
        final Handles handlesAnnotation = method.getAnnotation(Handles.class);
        assertThat(method.getName(), equalTo("postSomePathRecipeIdCakeshopCommandCreateRecipe"));
        assertThat(handlesAnnotation, not(nullValue()));
        assertThat(handlesAnnotation.value(), is("cakeshop.create-recipe"));

    }

    @Test
    public void shouldGenerateMethodAnnotatedWithHandlesAnnotationForPUT() throws Exception {
        generator.run(
                restRamlWithDefaults()
                        .with(resource("/some/path/{recipeId}")
                                .with(httpAction(PUT, "application/vnd.cakeshop.command.update-recipe+json")
                                        .withDescription(PUT_MAPPING_ANNOTATION))
                        ).build(),
                configurationWithBasePackage(BASE_PACKAGE, outputFolder, generatorProperties().withServiceComponentOf("CUSTOM_COMPONENT")));

        final Class<?> clazz = compiler.compiledClassOf(BASE_PACKAGE, "RemoteCustomComponent2ServiceCommandApi");
        final List<Method> methods = methodsOf(clazz);
        assertThat(methods, hasSize(1));

        final Method method = methods.get(0);
        assertThat(method.getName(), equalTo("putSomePathRecipeIdCakeshopCommandUpdateRecipe"));
        final Handles handlesAnnotation = method.getAnnotation(Handles.class);
        assertThat(handlesAnnotation, not(nullValue()));
        assertThat(handlesAnnotation.value(), is("cakeshop.update-recipe"));

    }

    @Test
    public void shouldGenerateMethodAnnotatedWithHandlesAnnotationForPATCH() throws Exception {
        generator.run(
                restRamlWithDefaults()
                        .with(resource("/some/path/{recipeId}")
                                .with(httpAction(PATCH, "application/vnd.cakeshop.command.patch-recipe+json")
                                        .withDescription(PATCH_MAPPING_ANNOTATION))
                        ).build(),
                configurationWithBasePackage(BASE_PACKAGE, outputFolder, generatorProperties().withServiceComponentOf("CUSTOM_COMPONENT")));

        final Class<?> clazz = compiler.compiledClassOf(BASE_PACKAGE, "RemoteCustomComponent2ServiceCommandApi");
        final List<Method> methods = methodsOf(clazz);
        assertThat(methods, hasSize(1));

        final Method method = methods.get(0);
        assertThat(method.getName(), equalTo("patchSomePathRecipeIdCakeshopCommandPatchRecipe"));
        final Handles handlesAnnotation = method.getAnnotation(Handles.class);
        assertThat(handlesAnnotation, not(nullValue()));
        assertThat(handlesAnnotation.value(), is("cakeshop.patch-recipe"));

    }

    @Test
    public void shouldGenerateMethodAnnotatedWithHandlesAnnotationForDELETE() throws Exception {
        generator.run(
                restRamlWithDefaults()
                        .with(resource("/some/path/{recipeId}")
                                .with(httpAction(DELETE, "application/vnd.cakeshop.command.delete-recipe+json")
                                        .withDescription(DELETE_MAPPING_ANNOTATION))
                        ).build(),
                configurationWithBasePackage(BASE_PACKAGE, outputFolder, generatorProperties().withServiceComponentOf("CUSTOM_COMPONENT")));

        final Class<?> clazz = compiler.compiledClassOf(BASE_PACKAGE, "RemoteCustomComponent2ServiceCommandApi");
        final List<Method> methods = methodsOf(clazz);
        assertThat(methods, hasSize(1));

        final Method method = methods.get(0);
        assertThat(method.getName(), equalTo("deleteSomePathRecipeIdCakeshopCommandDeleteRecipe"));
        final Handles handlesAnnotation = method.getAnnotation(Handles.class);
        assertThat(handlesAnnotation, not(nullValue()));
        assertThat(handlesAnnotation.value(), is("cakeshop.delete-recipe"));

    }

    @Test
    public void shouldGenerateMethodAcceptingEnvelope() throws MalformedURLException {
        generator.run(
                restRamlWithDefaults()
                        .with(resource("/some/path/{recipeId}")
                                .with(httpAction(POST, "application/vnd.cakeshop.command.create-recipe+json")
                                        .withDescription(POST_MAPPING_ANNOTATION))
                        ).build(),
                configurationWithBasePackage(BASE_PACKAGE, outputFolder, generatorProperties().withServiceComponentOf("CUSTOM_COMPONENT")));

        final Class<?> clazz = compiler.compiledClassOf(BASE_PACKAGE, "RemoteCustomComponent2ServiceCommandApi");
        final Method method = firstMethodOf(clazz).get();
        assertThat(method.getName(), equalTo("postSomePathRecipeIdCakeshopCommandCreateRecipe"));
        assertThat(method.getParameterCount(), is(1));
        assertThat(method.getParameters()[0].getType(), equalTo((JsonEnvelope.class)));
    }

    @Test
    public void shouldThrowExceptionIfServiceComponentPropertyNotSet() {

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("serviceComponent generator property not set in the plugin config");

        generator.run(
                restRamlWithDefaults()
                        .withDefaultPostResource()
                        .build(),
                configurationWithBasePackage(BASE_PACKAGE, outputFolder, new CommonGeneratorProperties()));
    }

    @Test
    public void shouldThrowExceptionIfBaseUriHasLessThanEightParts() {

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(containsString("baseUri must have 8 parts"));

        generator.run(
                restRamlWithTitleVersion().withBaseUri(BASE_URI_WITH_LESS_THAN_EIGHT_PARTS).build(),
                configurationWithBasePackage(BASE_PACKAGE, outputFolder, generatorProperties().withServiceComponentOf("CUSTOM_COMPONENT")));

    }

    @Test
    public void shouldThrowExceptionIfBaseUriHasMoreThanEightParts() {

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(containsString("baseUri must have 8 parts"));

        generator.run(
                restRamlWithTitleVersion().withBaseUri(BASE_URI_WITH_MORE_THAN_EIGHT_PARTS).build(),
                configurationWithBasePackage(BASE_PACKAGE, outputFolder, generatorProperties().withDefaultServiceComponent()));

    }

    @Test
    public void shouldGenerateClassIfBaseUriContainsHyphens() throws Exception {
        generator.run(
                raml()
                        .withBaseUri(BASE_URI_WITH_HYPHENATED_SERVICE_NAME)
                        .withDefaultPostResource()
                        .build(),
                configurationWithBasePackage(BASE_PACKAGE, outputFolder, generatorProperties().withServiceComponentOf("EVENT_PROCESSOR")));

        compiler.compiledClassOf(BASE_PACKAGE, "RemoteEventProcessor2ServiceWithHyphensCommandApi");
    }

    @Test
    public void shouldIgnoreNonVendorSpecificMediaTypes() throws Exception {
        generator.run(
                restRamlWithQueryApiDefaults()
                        .with(resource("/pathabc/{anId}")
                                .with(httpAction()
                                        .withHttpActionType(GET)
                                        .withResponseTypes("text/csv")
                                        .with(mapping()
                                                .withName("action1")
                                                .withResponseType("text/csv"))
                                ))
                        .with(resource("/pathbcd/{anId}")
                                .with(httpAction()
                                        .withHttpActionType(GET)
                                        .withResponseTypes("application/abc+json")
                                        .with(mapping()
                                                .withName("action2")
                                                .withResponseType("application/abc+json"))
                                ))

                        .build(),
                configurationWithBasePackage(BASE_PACKAGE, outputFolder, generatorProperties().withServiceComponentOf("SOME_COMPONENT")));


        assertThat(methodsOf(compiler.compiledClassOf(BASE_PACKAGE, "RemoteSomeComponent2ServiceQueryApi")), hasSize(0));

    }

    private void assertBaseUriField(final Field field) {
        assertThat(Modifier.isStatic(field.getModifiers()), is(true));
        assertThat(Modifier.isPrivate(field.getModifiers()), is(true));
        assertThat(Modifier.isFinal(field.getModifiers()), is(true));
    }

    private void assertRestClientField(final Field field) {
        assertThat(field.getAnnotation(Inject.class), not(nullValue()));
    }

    private void assertRestClientHelperField(final Field field) {
        assertThat(field.getAnnotation(Inject.class), not(nullValue()));
    }

    private void assertEnveloperField(final Field field) {
        assertThat(field.getAnnotation(Inject.class), not(nullValue()));
    }

}
