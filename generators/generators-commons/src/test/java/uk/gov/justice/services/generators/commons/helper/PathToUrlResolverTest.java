package uk.gov.justice.services.generators.commons.helper;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class PathToUrlResolverTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldResolvePathToUrl() {
        final Path baseDir = Paths.get("/yaml");
        final Path path = Paths.get("unified-search-descriptor.yaml");

        final URL url = new PathToUrlResolver().resolveToUrl(baseDir, path);

        assertThat(url.toString(), is("file:/yaml/unified-search-descriptor.yaml"));
    }

    @Test
    public void shouldThrowUnifiedSearchExceptionWhenResolutionFailsForPathToUrl() {

        expectedException.expectMessage("Cannot resolve path as URL nul");
        expectedException.expect(FileParserException.class);
        final PathToUrlResolver pathToUrlResolver = spy(PathToUrlResolver.class);
        final Path basedir = mock(Path.class);
        final Path url = mock(Path.class);

        given(basedir.resolve(url)).willAnswer(invocation -> {
            throw new MalformedURLException("oops");
        });

        pathToUrlResolver.resolveToUrl(basedir, url);


    }

}