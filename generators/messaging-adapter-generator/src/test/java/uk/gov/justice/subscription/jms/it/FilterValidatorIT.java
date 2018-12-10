package uk.gov.justice.subscription.jms.it;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static uk.gov.justice.services.messaging.jms.HeaderConstants.JMS_HEADER_CPPNAME;

import uk.gov.justice.api.subscription.Service2EventListenerAnotherPeopleEventEventFilter;
import uk.gov.justice.api.subscription.Service2EventListenerAnotherPeopleEventEventValidationInterceptor;
import uk.gov.justice.services.adapter.messaging.DefaultJmsParameterChecker;
import uk.gov.justice.services.adapter.messaging.JsonSchemaValidationInterceptor;
import uk.gov.justice.services.cdi.LoggerProducer;
import uk.gov.justice.services.core.json.DefaultJsonValidationLoggerHelper;
import uk.gov.justice.services.core.json.JsonSchemaValidator;
import uk.gov.justice.services.core.mapping.DefaultNameToMediaTypeConverter;
import uk.gov.justice.services.core.mapping.MediaType;
import uk.gov.justice.services.messaging.logging.DefaultJmsMessageLoggerHelper;

import java.util.Optional;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.openejb.jee.WebApp;
import org.apache.openejb.junit.ApplicationComposer;
import org.apache.openejb.testing.Classes;
import org.apache.openejb.testing.Module;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ApplicationComposer.class)
public class FilterValidatorIT extends AbstractJmsAdapterGenerationIT {

    private static final String PEOPLE_EVENT_AA = "people.eventaa";

    @Inject
    private RecordingJsonSchemaValidator recordingJsonSchemaValidator;

    @Inject
    private Service2EventListenerAnotherPeopleEventEventValidationInterceptor eventListenerValidationInterceptor;

    @Resource(name = "another.people.event")
    private Topic peopleEventsDestination;

    @Module
    @Classes(cdi = true, value = {
            Service2EventListenerAnotherPeopleEventEventFilter.class,
            Service2EventListenerAnotherPeopleEventEventValidationInterceptor.class,

            JsonSchemaValidationInterceptor.class,
            LoggerProducer.class,
            DefaultJmsParameterChecker.class,
            DefaultJsonValidationLoggerHelper.class,
            DefaultJmsMessageLoggerHelper.class,
            RecordingJsonSchemaValidator.class,
            DefaultNameToMediaTypeConverter.class

    })
    public WebApp war() {
        return new WebApp()
                .contextRoot("subscription.JmsAdapterToHandlerIT");
    }

    @Before
    public void setup() throws Exception {
        cleanQueue(peopleEventsDestination);
    }

    @Test
    public void shouldValidate() throws Exception {

        final boolean result = eventListenerValidationInterceptor.shouldValidate(textMessage(getSession(), PEOPLE_EVENT_AA));

        assertThat(result, is(true));
        assertThat(recordingJsonSchemaValidator.validatedEventName(), is(PEOPLE_EVENT_AA));
    }

    @Test
    public void shouldNotValidate() throws Exception {

        final boolean result = eventListenerValidationInterceptor.shouldValidate(textMessage(getSession(), "people.unsuported-event"));

        assertThat(result, is(false));
        assertThat(recordingJsonSchemaValidator.validatedEventName(), is(nullValue()));
    }

    private TextMessage textMessage(final Session session, final String eventName) throws Exception {
        final TextMessage textMessage = session.createTextMessage("textMessage");
        textMessage.setStringProperty(JMS_HEADER_CPPNAME, eventName);
        return textMessage;
    }

    @ApplicationScoped
    public static class RecordingJsonSchemaValidator implements JsonSchemaValidator {

        private String validatedEventName;

        @Override
        public void validate(final String payload, final String actionName) {
            this.validatedEventName = actionName;
        }

        @Override
        public void validate(final String payload, final String actionName, final Optional<MediaType> mediaType) {
            this.validatedEventName = actionName;
        }

        public String validatedEventName() {
            return validatedEventName;
        }
    }
}
