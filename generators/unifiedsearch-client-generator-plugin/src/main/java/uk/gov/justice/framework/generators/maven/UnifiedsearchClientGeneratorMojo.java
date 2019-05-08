package uk.gov.justice.framework.generators.maven;

import static org.apache.maven.plugins.annotations.ResolutionScope.COMPILE_PLUS_RUNTIME;

import uk.gov.justice.maven.generator.io.files.parser.generator.GenerateMojo;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "generate-unifiedsearch-client", requiresDependencyResolution = COMPILE_PLUS_RUNTIME, defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class UnifiedsearchClientGeneratorMojo extends GenerateMojo {
}
