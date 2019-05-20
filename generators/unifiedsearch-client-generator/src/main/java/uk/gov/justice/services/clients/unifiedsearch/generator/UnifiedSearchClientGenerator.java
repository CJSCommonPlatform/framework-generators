package uk.gov.justice.services.clients.unifiedsearch.generator;

import static java.lang.String.format;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static uk.gov.justice.services.generators.commons.config.GeneratorPropertiesHelper.serviceComponentOf;
import static uk.gov.justice.services.generators.commons.helper.GeneratedClassWriter.writeClass;

import uk.gov.justice.maven.generator.io.files.parser.core.Generator;
import uk.gov.justice.maven.generator.io.files.parser.core.GeneratorConfig;
import uk.gov.justice.services.clients.unifiedsearch.core.domain.Event;
import uk.gov.justice.services.clients.unifiedsearch.core.domain.UnifiedSearchDescriptor;
import uk.gov.justice.services.core.annotation.Handles;
import uk.gov.justice.services.core.annotation.ServiceComponent;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.messaging.logging.TraceLogger;
import uk.gov.justice.services.unifiedsearch.UnifiedSearchIndexer;
import uk.gov.justice.services.unifiedsearch.UnifiedSearchName;

import java.util.List;
import java.util.stream.Stream;

import javax.inject.Inject;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnifiedSearchClientGenerator implements Generator<UnifiedSearchDescriptor> {

    private static final String TRACE_LOGGER_FIELD = "traceLogger";

    private Logger logger = LoggerFactory.getLogger(UnifiedSearchClientGenerator.class);

    private static final String ENVELOPE = "jsonEnvelope";


    @Override
    public void run(final UnifiedSearchDescriptor unifiedSearchDescriptor, final GeneratorConfig generatorConfig) {

        final List<Event> eventList = unifiedSearchDescriptor.getEvents();
        final String serviceName = unifiedSearchDescriptor.getService();
        final ClassNameFactory classNameFactory = new ClassNameFactory(generatorConfig.getBasePackageName(), serviceName);

        final Stream<TypeSpec> generatedClasses = eventList.stream().map(event -> generatedClassesFrom(event, classNameFactory, generatorConfig));
        generatedClasses.forEach(generatedClass -> writeClass(generatorConfig, generatorConfig.getBasePackageName(), generatedClass, logger));
    }

    private TypeSpec generatedClassesFrom(final Event event,
                                      final ClassNameFactory classNameFactory,
                                      final GeneratorConfig generatorConfig) {


        final String className = classNameFactory.classNameFor().simpleName();
        final TypeSpec.Builder serviceComponentTypeSpec = serviceComponentTypeSpec(generatorConfig, className);
        final TypeSpec.Builder builder = generateServiceComponentClassSpec(event, serviceComponentTypeSpec, className);

        return builder.build();
    }


    private TypeSpec.Builder generateServiceComponentClassSpec(final Event event,
                                                               final TypeSpec.Builder classSpec,
                                                               final String className) {
        fieldSpec(classSpec, event, className);
        methodSpec(classSpec, event);

        return classSpec;
    }

    private TypeSpec.Builder serviceComponentTypeSpec(final GeneratorConfig generatorConfig, final String className) {
        final String serviceComponent = serviceComponentOf(generatorConfig);

        return TypeSpec.classBuilder(className)
                .addModifiers(PUBLIC, FINAL)
                .addAnnotation(AnnotationSpec.builder(ServiceComponent.class)
                        .addMember("value", "$S", serviceComponent)
                        .build());
    }


    private FieldSpec loggerConstantField(final String className) {
        final ClassName classLoggerFactory = ClassName.get(LoggerFactory.class);
        return FieldSpec.builder(Logger.class, "LOGGER")
                .addModifiers(PRIVATE, javax.lang.model.element.Modifier.STATIC, FINAL)
                .initializer(
                        CodeBlock.builder()
                                .add(format("$L.getLogger(%s.class)", className), classLoggerFactory).build()
                )
                .build();
    }

    private void fieldSpec(final TypeSpec.Builder classSpec,
                           final Event event,
                           final String className) {
        classSpec.addField(loggerConstantField(className));
        classSpec.addField(FieldSpec.builder(TraceLogger.class,
                TRACE_LOGGER_FIELD)
                .addModifiers(PRIVATE)
                .addAnnotation(Inject.class)
                .build());
        classSpec.addField(FieldSpec.builder(UnifiedSearchIndexer.class, "unifiedSearchIndexer")
                .addAnnotation(Inject.class)
                .addModifiers(PRIVATE)
                .addAnnotation(AnnotationSpec.builder(UnifiedSearchName.class)
                        .addMember("value", "$S", event.getIndexName())
                        .build()).build());

        classSpec.addField(
                FieldSpec.builder(UnifiedSearchAdapter.class, "unifiedSearchAdapter")
                        .addModifiers(PRIVATE)
                        .addAnnotation(Inject.class).build());

    }

    private void methodSpec(final TypeSpec.Builder classSpec, final Event event) {
        classSpec.addMethod(MethodSpec.methodBuilder("handle")
                .addModifiers(PUBLIC)
                .addAnnotation(AnnotationSpec.builder(Handles.class)
                        .addMember("value", "$S", event.getName())
                        .build())
                .addParameter(ParameterSpec.builder(JsonEnvelope.class, ENVELOPE)
                        .addModifiers(FINAL)
                        .build())
                .addStatement("unifiedSearchAdapter.index(jsonEnvelope, unifiedSearchIndexer);")
                .addStatement("$L.trace(LOGGER, () -> String.format(\"Handling remote request: %s\", jsonEnvelope))",
                        TRACE_LOGGER_FIELD)
                .build());
    }
}
