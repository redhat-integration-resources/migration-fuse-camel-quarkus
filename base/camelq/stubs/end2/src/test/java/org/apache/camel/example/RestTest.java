package org.apache.camel.example;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.quarkus.test.CamelQuarkusTestSupport;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;


@QuarkusTest
public class RestTest extends CamelQuarkusTestSupport {

    @Inject
    CamelContext context;

    @Override
    protected CamelContext createCamelContext() throws Exception {
        return this.context;
    }

    @Test
    public void subscriber() throws Exception {

        //prepare JSON call and set expectations
        given()
            .body("{\"id\": \"123\"}")
            .header("Content-Type", "application/json")
        .when()
            .post("/camel/subscriber/details")
        .then()
            // .log().all()
            .statusCode(200)
            .body(
                "subscriptions.period.start", is("01-01-2023"),
                "subscriptions.period.end",   is("01-01-2024"),

                "subscriptions.packages.size()",is(3),
                "subscriptions.packages[0].id", is("i-001"),
                "subscriptions.packages[1].id", is("i-002"),
                "subscriptions.packages[2].id", is("i-018"),

                "subscriptions.packages[0].amount", is(16),
                "subscriptions.packages[1].amount", is(32),
                "subscriptions.packages[2].amount", is(200)
            );
    }    
}
