package uk.gov.justice.services.clients.unifiedsearch.core;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.net.URL;

import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.io.IOUtils;

@ApplicationScoped
public class FileContentsAsStringLoader {

    public String readFileContents(final URL urlOfFile) {

        try  {
            return IOUtils.toString(urlOfFile, UTF_8);
        } catch (final IOException e) {
            throw new UnifiedSearchException(format("Failed to read file contents from '%s'", urlOfFile), e);
        }
    }
}
