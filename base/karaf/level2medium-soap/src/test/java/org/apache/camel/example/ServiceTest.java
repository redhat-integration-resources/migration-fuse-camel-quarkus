package org.apache.camel.example;

import java.net.ServerSocket;
import java.util.Properties;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;

import org.apache.camel.spi.Language;

import org.junit.Test;
import org.junit.Ignore;

public class ServiceTest extends CamelBlueprintTestSupport {
	
    @Override
    protected String getBlueprintDescriptor() {
        String f = "OSGI-INF/blueprint/";
        return 
             f+"beans.xml,"
            +f+"camel.xml,"
            +f+"cxf.xml,"
            +f+"configuration.xml";
    }

    public static int getRandomPort() {
        try (ServerSocket s = new ServerSocket(0)) {
            return s.getLocalPort();
        } catch (Exception e) {
            throw new RuntimeException("Can't get random port");
        }
    }

    @Override
    protected String setConfigAdminInitialConfiguration(final Properties props) {
    	String restPort = Integer.toString(getRandomPort());
    	String soapPort = Integer.toString(getRandomPort());
        props.setProperty("rest.host",         "localhost");
        props.setProperty("rest.port",         restPort);
        props.setProperty("api.backend1.host", "localhost:"+restPort);
        props.setProperty("api.backend1.path", "/unit/test/subscriber/details");
        props.setProperty("api.backend2.host", "localhost:"+soapPort);
        return "demo.medium";
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {

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
		String uri = "http://{{rest.host}}:{{rest.port}}/camel/subscriber/details";

		//invoke service
		String jsonResponse = template.requestBody(uri, "{\"id\":\"123\"}", String.class);

        //validate stub expectations
        MockEndpoint.assertIsSatisfied(context);

        //obtain request sent to REST backend
        String restMessage = 
                rest.getExchanges().get(0).getIn().getBody(String.class);

        //validate request to REST backend
        assertEquals("oh oh", "{\"id\":\"123\"}", restMessage);

        //obtain request sent to SOAP backend
        org.example.s1.SubscriberRequest soapMessage = 
                soap.getExchanges().get(0).getIn().getBody(org.example.s1.SubscriberRequest.class);

        //validate request to SOAP backend
        assertNotNull("something is wrong.", soapMessage);
        assertEquals("oh oh", "123", soapMessage.getId());

        //prepare exchange to test JSON assertions
        DefaultExchange response = new DefaultExchange(context);
        response.getIn().setBody(jsonResponse);

		//prepare JsonPath language
		Language lan = context.resolveLanguage("jsonpath");

		//validate response
		assertEquals("ups", "Some One",            lan.createExpression("client.fullName")    .evaluate(response, String.class));
		assertEquals("ups", "1 Some Street",       lan.createExpression("client.addressLine1").evaluate(response, String.class));
		assertEquals("ups", "Somewhere SOME C0D3", lan.createExpression("client.addressLine2").evaluate(response, String.class));
		assertEquals("ups", "UK",                  lan.createExpression("client.addressLine3").evaluate(response, String.class));

		assertEquals("ups", "01-01-2023", lan.createExpression("subscriptions.period.start").evaluate(response, String.class));
		assertEquals("ups", "01-01-2024", lan.createExpression("subscriptions.period.end")  .evaluate(response, String.class));

		assertEquals("ups", "i-001", lan.createExpression("subscriptions.packages[0].id").evaluate(response, String.class));
		assertEquals("ups", "i-002", lan.createExpression("subscriptions.packages[1].id").evaluate(response, String.class));
		assertEquals("ups", "i-018", lan.createExpression("subscriptions.packages[2].id").evaluate(response, String.class));

        assertEquals("ups", new Integer(16), lan.createExpression("subscriptions.packages[0].amount").evaluate(response, Integer.class));
        assertEquals("ups", new Integer(32), lan.createExpression("subscriptions.packages[1].amount").evaluate(response, Integer.class));
        assertEquals("ups", new Integer(200),lan.createExpression("subscriptions.packages[2].amount").evaluate(response, Integer.class));
	}
}