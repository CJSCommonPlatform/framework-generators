package uk.gov.justice.services.adapters.rest.generator;

import static com.squareup.javapoet.TypeSpec.classBuilder;
import static java.util.Collections.singletonList;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;
import static uk.gov.justice.services.generators.commons.helper.Names.buildJavaFriendlyName;

import uk.gov.justice.maven.generator.io.files.parser.core.GeneratorConfig;
import uk.gov.justice.services.adapter.rest.filter.LoggerRequestDataAdder;
import uk.gov.justice.services.generators.commons.config.CommonGeneratorProperties;
import uk.gov.justice.services.generators.commons.helper.RestResourceBaseUri;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.model.Raml;

public class LoggerRequestDataFilterGenerator {

    private static final String LOGGER_REQUEST_DATA_FILTER_CLASS_SUFFIX = "LoggerRequestDataFilter";

    public List<TypeSpec> generateFor(final Raml raml, final GeneratorConfig generatorConfig) {

        final CommonGeneratorProperties commonGeneratorProperties = (CommonGeneratorProperties) generatorConfig.getGeneratorProperties();
        final String serviceComponent = commonGeneratorProperties.getServiceComponent();
        final RestResourceBaseUri baseUri = new RestResourceBaseUri(raml.getBaseUri());

        return singletonList(classBuilder(classNameFor(baseUri))
                .addModifiers(PUBLIC)
                .addAnnotation(Provider.class)
                .addSuperinterface(ContainerRequestFilter.class)
                .addSuperinterface(ContainerResponseFilter.class)
                .addField(FieldSpec.builder(ClassName.get(LoggerRequestDataAdder.class), "loggerRequestDataAdder")
                        .addAnnotation(Inject.class)
                        .build())
                .addMethod(generateRequestFilterMethod(serviceComponent))
                .addMethod(generateResponseFilterMethod(serviceComponent))
                .build());
    }

    /**
     * Generate the request filter method.
     *
     * @return the {@link MethodSpec} that represents the request filter method
     */
    private MethodSpec generateRequestFilterMethod(final String serviceComponent) {

        final String containerRequestContextParameter = "containerRequestContext";

        return MethodSpec.methodBuilder("filter")
                .addModifiers(PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(ParameterSpec
                        .builder(ContainerRequestContext.class, containerRequestContextParameter, FINAL)
                        .build())
                .addException(IOException.class)
                .addCode(CodeBlock.builder()
                        .addStatement("$L.addToMdc($L, $S)",
                                "loggerRequestDataAdder",
                                containerRequestContextParameter,
                                serviceComponent
                        )
                        .build())
                .build();
    }

    /**
     * Generate the response filter method.
     *
     * @return the {@link MethodSpec} that represents the response filter method
     */
    private MethodSpec generateResponseFilterMethod(final String serviceComponent) {

        final String containerRequestContextParameter = "containerRequestContext";
        final String containerResponseContextParameter = "containerResponseContext";

        return MethodSpec.methodBuilder("filter")
                .addModifiers(PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(ParameterSpec
                        .builder(ContainerRequestContext.class, containerRequestContextParameter, FINAL)
                        .build())
                .addParameter(ParameterSpec
                        .builder(ContainerResponseContext.class, containerResponseContextParameter, FINAL)
                        .build())
                .addCode(CodeBlock.builder()
                        .addStatement("$L.clearMdc()", "loggerRequestDataAdder")
                        .build())
                .build();
    }

    private static String classNameFor(final RestResourceBaseUri baseUri) {
        return buildJavaFriendlyName(baseUri.pathWithoutWebContext())
                .concat(LOGGER_REQUEST_DATA_FILTER_CLASS_SUFFIX);
    }
}
