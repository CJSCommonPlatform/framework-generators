package uk.gov.justice.services.clients.unifiedsearch.generator;

import static uk.gov.justice.services.generators.commons.helper.Names.buildJavaFriendlyName;

import com.squareup.javapoet.ClassName;

public class ClassNameFactory {

    public static final String EVENT_INDEXER = "EventIndexer";

    private int classNameVersion;

    private final String basePackageName;
    private final String contextName;


    public ClassNameFactory(final String basePackageName,
                            final String contextName) {
        this.basePackageName = basePackageName;
        this.contextName = contextName;
    }

    /**
     * Convert given URI and component to a camel cased class name
     *
     * @return Java Poet class name
     */
    public ClassName classNameFor() {

        final String simpleName =
                buildJavaFriendlyName(contextName) +
                        EVENT_INDEXER +
                 getVersion();

        return ClassName.get(basePackageName, simpleName);
    }

    private String getVersion(){
        return String.valueOf(++classNameVersion);
    }

}
