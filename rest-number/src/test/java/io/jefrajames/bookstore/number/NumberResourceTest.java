package io.jefrajames.bookstore.number;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.collection.IsMapContaining.hasKey;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class NumberResourceTest {

    @Test
    void shouldGenerateBookNumber() {
        given()
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
        .when()
                .get("/api/numbers/book")
        .then()
                .statusCode(OK.getStatusCode())
                .body("$", hasKey("isbn_10"))
                .body("$", hasKey("isbn_13"))
                .body("$", hasKey("asin"))
                .body("$", hasKey("ean_8"))
                .body("$", hasKey("ean_13"))
                .body("$", not(hasKey("generationDate")));
    }
    
}
