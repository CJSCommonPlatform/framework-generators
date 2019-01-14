package uk.gov.justice.services.adapters.rest.generator;

import static com.squareup.javapoet.TypeSpec.classBuilder;
import static javax.lang.model.element.Modifier.PUBLIC;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static uk.gov.justice.services.generators.commons.helper.Names.buildJavaFriendlyName;

import uk.gov.justice.services.adapter.rest.application.CommonProviders;
import uk.gov.justice.services.generators.commons.helper.RestResourceBaseUri;

import java.util.Collection;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;
import org.raml.model.Raml;

/**
 * Internal code generation class for generating the JAX-RS {@link Application} that ties the
 * resources to a base URI.
 */
class JaxRsApplicationCodeGenerator {

    private static final String DEFAULT_ANNOTATION_PARAMETER = "value";
    private static final String COMMON_PROVIDERS_FIELD = "commonProviders";
    private static final String APPLICATION_NAME_SUFFIX = "Application";

    /**
     * Create an implementation of the {@link Application}.
     *
     * @param raml       the RAML document being generated from
     * @param classNames a collection of fully qualified class names of the resource implementation
     *                   classes
     * @return the fully defined application class
     */
    TypeSpec generateFor(final Raml raml, final Collection<ClassName> classNames) {
        return classSpecFrom(raml)
                .addMethod(generateGetClassesMethod(classNames))
                .build();
    }

    /**
     * Generate the implementation of {@link Application} and set the {@link ApplicationPath}.
     *
     * @param raml the RAML document being generated from
     * @return the {@link TypeSpec.Builder} that defines the class
     */
    private TypeSpec.Builder classSpecFrom(final Raml raml) {
        final RestResourceBaseUri baseUri = new RestResourceBaseUri(raml.getBaseUri());
        return classBuilder(applicationNameFrom(baseUri))
                .addModifiers(PUBLIC)
                .superclass(Application.class)
                .addField(FieldSpec.builder(ClassName.get(CommonProviders.class), COMMON_PROVIDERS_FIELD)
                        .addAnnotation(Inject.class)
                        .build())
                .addAnnotation(AnnotationSpec.builder(ApplicationPath.class)
                        .addMember(DEFAULT_ANNOTATION_PARAMETER, "$S", defaultIfBlank(baseUri.pathWithoutWebContext(), "/"))
                        .build());
    }

    /**
     * Generate the getClasses method that returns the set of implemented resource classes.
     *
     * @param classNames the collection of implementation class names
     * @return the {@link MethodSpec} that represents the getClasses method
     */
    private MethodSpec generateGetClassesMethod(final Collection<ClassName> classNames) {
        final ParameterizedTypeName wildcardClassType = ParameterizedTypeName.get(ClassName.get(Class.class), WildcardTypeName.subtypeOf(Object.class));
        final ParameterizedTypeName classSetType = ParameterizedTypeName.get(ClassName.get(Set.class), wildcardClassType);

        return MethodSpec.methodBuilder("getClasses")
                .addModifiers(PUBLIC)
                .addAnnotation(Override.class)
                .addCode(CodeBlock.builder()
                        .addStatement("$T classes = $L.providers()", classSetType, COMMON_PROVIDERS_FIELD)
                        .add(statementsToAddClassToSetForEach(classNames))
                        .addStatement("return classes")
                        .build())
                .returns(classSetType)
                .build();
    }

    /**
     * Generate code to add each resource implementation class to the classes hash set.
     *
     * @param classNames the collection of implementation class names
     * @return the {@link CodeBlock} that represents the generated statements
     */
    private CodeBlock statementsToAddClassToSetForEach(final Collection<ClassName> classNames) {
        final CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();

        classNames.forEach(implementationClassName ->
                codeBlockBuilder.addStatement("classes.add($T.class)", implementationClassName));

        return codeBlockBuilder.build();
    }

    private static String applicationNameFrom(final RestResourceBaseUri baseUri) {
        return buildJavaFriendlyName(baseUri.pathWithoutWebContext())
                .concat(APPLICATION_NAME_SUFFIX);
    }
}
