package uk.gov.justice.raml.maven.lintchecker.rules.handlers;

import static uk.gov.justice.services.core.annotation.Component.COMMAND_API;

import uk.gov.justice.services.core.annotation.Handles;
import uk.gov.justice.services.core.annotation.ServiceComponent;
import uk.gov.justice.services.messaging.JsonEnvelope;

@ServiceComponent(COMMAND_API)
public class TestHandlers {

    @Handles("test.firstcommand")
    public void testFirstCommand(final JsonEnvelope command) {}

    @Handles("test.secondcommand")
    public void testSecondCommand(final JsonEnvelope command) {}
}
