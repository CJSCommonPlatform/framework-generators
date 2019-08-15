package uk.gov.justice.services.clients.messaging.subscription.generator;

import uk.gov.justice.maven.generator.io.files.parser.core.GeneratorFactory;
import uk.gov.justice.services.generators.subscription.parser.SubscriptionWrapper;

public class SubscriptionMessagingClientGeneratorFactory implements GeneratorFactory<SubscriptionWrapper> {

    @Override
    public SubscriptionMessagingClientGenerator create() {
        final MethodCodeGenerator methodCodeGenerator = new MethodCodeGenerator();
        final MethodsCodeGenerator methodsCodeGenerator = new MethodsCodeGenerator(methodCodeGenerator);
        final RemoteMessagingClientCodeGenerator remoteMessagingClientCodeGenerator = new RemoteMessagingClientCodeGenerator(methodsCodeGenerator);

        return new SubscriptionMessagingClientGenerator(remoteMessagingClientCodeGenerator);
    }
}
