package uk.gov.justice.services.generators.subscription.parser;

public class JmsUriToDestinationConverter {

    /**
     * Retrieves the destination from jmsUri
     *
     * @return messaging adapter clientId
     */
    public String convert(final String jmsUri) {
        return jmsUri.split(":")[2];
    }
}
