/*
 * Copyright 2021 jefrajames.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jefrajames.bookstore.number;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.github.javafaker.Faker;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import io.smallrye.mutiny.Uni;
import lombok.extern.java.Log;

/**
 *
 * @author jefrajames
 */
@Path("/book")
@Tag(name = "Number Endpoint")
@LogHttpHeaders
public class NumberResource {

    @ConfigProperty(name = "number.separator", defaultValue = "false")
    boolean separator;

    @ConfigProperty(name = "time.to.sleep", defaultValue = "15")
    int timeToSleep;

    @Timeout(250)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Generates book numbers", description = "These book numbers have several formats: ISBN, ASIN and EAN")
    @APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = BookNumberResponse.class)))
    public Uni<Response> generateBookNumbers() throws InterruptedException {

        if (timeToSleep != 0)
            TimeUnit.MILLISECONDS.sleep(timeToSleep);
            
        Faker faker = new Faker();
        BookNumberResponse bookNumbers = new BookNumberResponse();
        bookNumbers.setIsbn10(faker.code().isbn10(separator));
        bookNumbers.setIsbn13(faker.code().isbn13(separator));
        bookNumbers.setAsin(faker.code().asin());
        bookNumbers.setEan8(faker.code().ean8());
        bookNumbers.setEan13(faker.code().ean13());
        bookNumbers.setGenerationDate(Instant.now());

        return Uni.createFrom().item(Response.ok(bookNumbers).build());
    }

}
