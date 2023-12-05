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
        
        // COPY/PASTE HERE reconfiguration        
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                // COPY/PASTE HERE the embedded Camel routes
            }
        };
    }

    @Test
    public void testServiceGetDetails() throws Exception{

        // COPY/PASTE HERE REST checks <1>

        // COPY/PASTE HERE SOAP checks <2>

        // COPY/PASTE HERE JSON response checks <3>
    }
}