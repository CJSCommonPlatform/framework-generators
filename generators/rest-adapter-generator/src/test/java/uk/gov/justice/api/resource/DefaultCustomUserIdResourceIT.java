package uk.gov.justice.api.resource;

import static javax.ws.rs.core.Response.Status.ACCEPTED;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import uk.gov.justice.api.CustomApiRestExampleApplication;
import uk.gov.justice.api.mapper.DefaultCustomApiRestExampleCustomUserIdResourceActionMapper;
import uk.gov.justice.api.mapper.RestAdapterGeneratorMediaTypeToSchemaIdMapper;
import uk.gov.justice.schema.catalog.CatalogProducer;
import uk.gov.justice.schema.service.SchemaCatalogService;
import uk.gov.justice.services.adapter.rest.application.CommonProviders;
import uk.gov.justice.services.adapter.rest.application.DefaultCommonProviders;
import uk.gov.justice.services.adapter.rest.envelope.RestEnvelopeBuilderFactory;
import uk.gov.justice.services.adapter.rest.filter.LoggerRequestDataFilter;
import uk.gov.justice.services.adapter.rest.interceptor.JsonSchemaValidationInterceptor;
import uk.gov.justice.services.adapter.rest.mapper.BadRequestExceptionMapper;
import uk.gov.justice.services.adapter.rest.mapping.BasicActionMapperHelper;
import uk.gov.justice.services.adapter.rest.multipart.DefaultFileInputDetailsFactory;
import uk.gov.justice.services.adapter.rest.multipart.FileBasedInterceptorContextFactory;
import uk.gov.justice.services.adapter.rest.multipart.InputPartFileNameExtractor;
import uk.gov.justice.services.adapter.rest.parameter.ValidParameterCollectionBuilderFactory;
import uk.gov.justice.services.adapter.rest.processor.DefaultRestProcessor;
import uk.gov.justice.services.adapter.rest.processor.ResponseStrategyCache;
import uk.gov.justice.services.adapter.rest.processor.response.AcceptedStatusNoEntityResponseStrategy;
import uk.gov.justice.services.adapter.rest.processor.response.OkStatusEnvelopeEntityResponseStrategy;
import uk.gov.justice.services.adapter.rest.processor.response.OkStatusEnvelopePayloadEntityResponseStrategy;
import uk.gov.justice.services.adapter.rest.processor.response.ResponseStrategyHelper;
import uk.gov.justice.services.common.configuration.ServiceContextNameProvider;
import uk.gov.justice.services.common.converter.StringToJsonObjectConverter;
import uk.gov.justice.services.common.converter.jackson.ObjectMapperProducer;
import uk.gov.justice.services.core.cdi.LoggerProducer;
import uk.gov.justice.services.core.envelope.EnvelopeInspector;
import uk.gov.justice.services.core.envelope.MediaTypeProvider;
import uk.gov.justice.services.core.extension.BeanInstantiater;
import uk.gov.justice.services.core.json.BackwardsCompatibleJsonSchemaValidator;
import uk.gov.justice.services.core.json.DefaultFileSystemUrlResolverStrategy;
import uk.gov.justice.services.core.json.DefaultJsonValidationLoggerHelper;
import uk.gov.justice.services.core.json.FileBasedJsonSchemaValidator;
import uk.gov.justice.services.core.json.JsonSchemaLoader;
import uk.gov.justice.services.core.json.PayloadExtractor;
import uk.gov.justice.services.core.json.SchemaCatalogAwareJsonSchemaValidator;
import uk.gov.justice.services.core.mapping.ActionNameToMediaTypesMappingObserver;
import uk.gov.justice.services.core.mapping.DefaultMediaTypesMappingCache;
import uk.gov.justice.services.core.mapping.DefaultNameToMediaTypeConverter;
import uk.gov.justice.services.core.mapping.DefaultSchemaIdMappingCache;
import uk.gov.justice.services.core.mapping.MediaTypeToSchemaIdMapper;
import uk.gov.justice.services.core.mapping.SchemaIdMappingObserver;
import uk.gov.justice.services.generators.test.utils.interceptor.RecordingInterceptorChainProcessor;
import uk.gov.justice.services.messaging.DefaultJsonObjectEnvelopeConverter;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.messaging.logging.DefaultHttpTraceLoggerHelper;
import uk.gov.justice.services.messaging.logging.DefaultTraceLogger;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.openejb.jee.Application;
import org.apache.openejb.jee.WebApp;
import org.apache.openejb.junit.ApplicationComposer;
import org.apache.openejb.testing.Classes;
import org.apache.openejb.testing.Configuration;
import org.apache.openejb.testing.EnableServices;
import org.apache.openejb.testing.Module;
import org.apache.openejb.testng.PropertiesBuilder;
import org.apache.openejb.util.NetworkUtil;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Integration tests for the generated JAX-RS classes.
 */
@EnableServices("jaxrs")
@RunWith(ApplicationComposer.class)
public class DefaultCustomUserIdResourceIT {

    private static final String USER_MEDIA_TYPE = "application/vnd.people.user+json";
    private static final String BASE_URI_PATTERN = "http://localhost:%d/rest-adapter-generator/custom/api/rest/example";
    private static final String JSON = "{\"userUrn\" : \"test\"}";
    private static int port = -1;
    private static String BASE_URI;

    private CloseableHttpClient httpClient;

    @Inject
    RecordingInterceptorChainProcessor interceptorChainProcessor;

    @Inject
    CommonProviders commonProviders;

    @BeforeClass
    public static void beforeClass() {
        port = NetworkUtil.getNextAvailablePort();
        BASE_URI = String.format(BASE_URI_PATTERN, port);
    }

    @Before
    public void setup() {
        httpClient = HttpClients.createDefault();
    }

    @Configuration
    public Properties properties() {
        return new PropertiesBuilder()
                .p("httpejbd.port", Integer.toString(port))
                .build();
    }

    @Module
    @Classes(cdi = true, value = {
            DefaultRestProcessor.class,
            OkStatusEnvelopeEntityResponseStrategy.class,
            OkStatusEnvelopePayloadEntityResponseStrategy.class,
            AcceptedStatusNoEntityResponseStrategy.class,
            ResponseStrategyHelper.class,
            RestEnvelopeBuilderFactory.class,
            RecordingInterceptorChainProcessor.class,
            ObjectMapperProducer.class,
            DefaultJsonObjectEnvelopeConverter.class,
            DefaultCommonProviders.class,
            DummyCommonProviders.class,
            BadRequestExceptionMapper.class,
            JsonSchemaValidationInterceptor.class,
            LoggerRequestDataFilter.class,
            TestServiceContextNameProvider.class,
            FileBasedJsonSchemaValidator.class,
            DefaultCustomApiRestExampleCustomUserIdResourceActionMapper.class,
            BasicActionMapperHelper.class,
            LoggerProducer.class,
            StringToJsonObjectConverter.class,
            FileBasedInterceptorContextFactory.class,
            InputPartFileNameExtractor.class,
            DefaultFileInputDetailsFactory.class,
            ResponseStrategyCache.class,
            ValidParameterCollectionBuilderFactory.class,
            JsonSchemaLoader.class,
            DefaultTraceLogger.class,
            DefaultHttpTraceLoggerHelper.class,
            DefaultFileSystemUrlResolverStrategy.class,

            RestAdapterGeneratorMediaTypeToSchemaIdMapper.class,
            SchemaCatalogAwareJsonSchemaValidator.class,
            PayloadExtractor.class,
            DefaultNameToMediaTypeConverter.class,
            DefaultSchemaIdMappingCache.class,
            SchemaIdMappingObserver.class,
            MediaTypeToSchemaIdMapper.class,
            BeanInstantiater.class,
            MediaTypeToSchemaIdMapper.class,

            CatalogProducer.class,
            SchemaCatalogService.class,

            DefaultMediaTypesMappingCache.class,
            ActionNameToMediaTypesMappingObserver.class,
            MediaTypeProvider.class,
            BackwardsCompatibleJsonSchemaValidator.class,
            EnvelopeInspector.class,
            DefaultJsonValidationLoggerHelper.class,

    })
    public WebApp war() {
        return new WebApp()
                .contextRoot("rest-adapter-generator")
                .addServlet("TestApp", Application.class.getName())
                .addInitParam("TestApp", "javax.ws.rs.Application", CustomApiRestExampleApplication.class.getName());
    }

    @Test
    public void shouldReturn202CreatingUser() throws Exception {
        final HttpResponse response = httpClient.execute(postRequestFor("/custom/1234", JSON, USER_MEDIA_TYPE));
        assertThat(response.getStatusLine().getStatusCode(), is(ACCEPTED.getStatusCode()));
    }

    @Test
    public void shouldDispatchCreateUserCommand() throws Exception {
        httpClient.execute(postRequestFor("/custom/567-8910", "{\"userUrn\" : \"1234\"}", USER_MEDIA_TYPE));

        final JsonEnvelope jsonEnvelope = interceptorChainProcessor.awaitForEnvelopeWithPayloadOf("userId", "567-8910");
        assertThat(jsonEnvelope.metadata().name(), is("people.create-user"));
        assertThat(jsonEnvelope.payloadAsJsonObject().getString("userId"), is("567-8910"));
        assertThat(jsonEnvelope.payloadAsJsonObject().getString("userUrn"), is("1234"));
    }

    @Test
    public void shouldReturn400ForJsonNotAdheringToSchema() throws Exception {
        final HttpResponse response = httpClient.execute(postRequestFor("/custom/1234", "{\"blah\" : \"1234\"}", USER_MEDIA_TYPE));
        assertThat(response.getStatusLine().getStatusCode(), is(BAD_REQUEST.getStatusCode()));
    }

    private HttpPost postRequestFor(final String uri, final String json, final String contentType) throws UnsupportedEncodingException {
        final HttpPost request = new HttpPost(BASE_URI + uri);
        request.setEntity(new StringEntity(json));
        request.setHeader("Content-Type", contentType);
        return request;
    }

    @ApplicationScoped
    public static class TestServiceContextNameProvider implements ServiceContextNameProvider {

        @Override
        public String getServiceContextName() {
            return "test-component";
        }
    }
}
