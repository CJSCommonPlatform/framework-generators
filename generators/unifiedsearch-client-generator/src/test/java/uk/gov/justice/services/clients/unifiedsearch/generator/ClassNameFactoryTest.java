package uk.gov.justice.services.clients.unifiedsearch.generator;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import com.squareup.javapoet.ClassName;
import org.junit.Test;

public class ClassNameFactoryTest {

    @Test
    public void shouldCreateClassNameFromBaseUriResourceUriAndClassNameSuffixForEventListener() {
        final String basePackageName = "base.package";
        final String contextName = "my-context";

        final ClassNameFactory classNameFactory = new ClassNameFactory(
                basePackageName,
                contextName);

        final ClassName className1 = classNameFactory.classNameFor();
        final ClassName className2 = classNameFactory.classNameFor();

        assertThat(className1.packageName(), is(basePackageName));
        assertThat(className2.packageName(), is(basePackageName));


        assertThat(className1.simpleName(), is("MyContextEventIndexer1"));
        assertThat(className2.simpleName(), is("MyContextEventIndexer2"));
    }


}
