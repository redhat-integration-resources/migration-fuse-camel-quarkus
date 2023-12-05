package org.apache.camel.example;

import jakarta.inject.Inject;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultExchange;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.quarkus.test.CamelQuarkusTestSupport;

import org.apache.camel.spi.Language;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class ServiceTest extends CamelQuarkusTestSupport {

    @Inject
    CamelContext context;

    @Override
    protected CamelContext createCamelContext() throws Exception {
        return this.context;
    }

    private void reconfigureEndpoints() throws Exception {

        //helper variable
        String host = "http://localhost:{{quarkus.http.test-port}}";

        //reconfigure REST endpoint
        AdviceWith.adviceWith(context, "call-system1", a -> {
          a.weaveById("end1").replace().to(host+"{{api.backend1.path}}");
        });

        //reconfigure SOAP endpoint
        AdviceWith.adviceWith(context, "call-system2", a -> {
          a.weaveById("end2").before()
           .setHeader("CamelDestinationOverrideUrl").simple(host+"/services/s1");
        });
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                //Quarkus random server port requires to reconfigure endpoints
                reconfigureEndpoints();

                rest("/unit/test")
                    .post("/subscriber/details")
                    .to("direct:backend-rest");

                from("direct:backend-rest")
                    .id("backend-rest-listener")
                    .to("mock:backend-rest")
                    .to("language:constant:resource:classpath:rest/response.json");

                from("cxf:bean:s1")
                    .id("backend-soap-listener")
                    .setBody().simple("${body[0]}")
                    .to("mock:backend-soap")
                    .log("Stub got request: ${body}")
                    .to("language:constant:resource:classpath:soap/response.xml");
            }
        };
    }

    @Test
    public void testServiceGetDetails() throws Exception{

        //set Mock expectations
        MockEndpoint rest = getMockEndpoint("mock:backend-rest"); 
        rest.expectedMessageCount(1);

        //set Mock expectations
        MockEndpoint soap = getMockEndpoint("mock:backend-soap"); 
        soap.expectedMessageCount(1);

        //prepare new request to destination Netty endpoint
        DefaultExchange request = new DefaultExchange(context);
        request.getIn().setBody("{\"id\":\"123\"}");

        //Entrypoint address
        String uri = "http://localhost:{{quarkus.http.test-port}}/camel/subscriber/details";

        //invoke service
        String jsonResponse = template.requestBody(uri, "{\"id\":\"123\"}", String.class);

        //validate stub expectations
        MockEndpoint.assertIsSatisfied(context);

        //obtain request sent to REST backend
        String restMessage = 
                rest.getExchanges().get(0).getIn().getBody(String.class);

        //validate request to REST backend
        assertEquals("{\"id\":\"123\"}", restMessage, "oh oh");

        //obtain request sent to SOAP backend
        org.example.s1.SubscriberRequest soapMessage = 
                soap.getExchanges().get(0).getIn().getBody(org.example.s1.SubscriberRequest.class);

        //validate request to SOAP backend
        assertNotNull(soapMessage, "something is wrong.");
        assertEquals("123", soapMessage.getId(), "oh oh");

        //prepare exchange to test JSON assertions
        DefaultExchange response = new DefaultExchange(context);
        response.getIn().setBody(jsonResponse);

        //prepare JsonPath language
        Language lan = context.resolveLanguage("jsonpath");

        //validate response
        assertEquals("Some One",            lan.createExpression("client.fullName")    .evaluate(response, String.class), "ups");
        assertEquals("1 Some Street",       lan.createExpression("client.addressLine1").evaluate(response, String.class), "ups");
        assertEquals("Somewhere SOME C0D3", lan.createExpression("client.addressLine2").evaluate(response, String.class), "ups");
        assertEquals("UK",                  lan.createExpression("client.addressLine3").evaluate(response, String.class), "ups");

        assertEquals("01-01-2023", lan.createExpression("subscriptions.period.start").evaluate(response, String.class), "ups");
        assertEquals("01-01-2024", lan.createExpression("subscriptions.period.end")  .evaluate(response, String.class), "ups");

        assertEquals("i-001", lan.createExpression("subscriptions.packages[0].id").evaluate(response, String.class), "ups");
        assertEquals("i-002", lan.createExpression("subscriptions.packages[1].id").evaluate(response, String.class), "ups");
        assertEquals("i-018", lan.createExpression("subscriptions.packages[2].id").evaluate(response, String.class), "ups");

        assertEquals(new Integer(16), lan.createExpression("subscriptions.packages[0].amount").evaluate(response, Integer.class), "ups");
        assertEquals(new Integer(32), lan.createExpression("subscriptions.packages[1].amount").evaluate(response, Integer.class), "ups");
        assertEquals(new Integer(200),lan.createExpression("subscriptions.packages[2].amount").evaluate(response, Integer.class), "ups");
    }
}