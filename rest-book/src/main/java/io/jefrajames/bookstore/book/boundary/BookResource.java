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
package io.jefrajames.bookstore.book.boundary;

import static javax.ws.rs.core.Response.Status.ACCEPTED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import io.jefrajames.bookstore.book.control.BookService;
import io.jefrajames.bookstore.book.control.DuplicateBookException;
import io.jefrajames.bookstore.book.entity.Book;
import io.jefrajames.bookstore.maintenance.boundary.CheckMaintenance;
import lombok.extern.java.Log;

/**
 * JAX-RS resource classes that expose the Book API.
 *
 * Thanks to the @CheckMaintenance annotation, it is not called if the site is
 * in maintenance.
 *
 * This class is implicitely traced by OpenAPI.
 *
 * @author jefrajames
 */
@Path("/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Book Endpoint")
@CheckMaintenance
@Log
public class BookResource {

    @Inject
    BookService service;

    @Operation(summary = "Returns all the books from the database")
    @APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Book.class, type = SchemaType.ARRAY)))
    @APIResponse(responseCode = "204", description = "No books")
    @APIResponse(responseCode = "503", description = "Service not available")
    @GET
    public Response findAllBooks() {
        List<Book> books = service.findAllBooks();
        return Response.ok(books).build();
    }

    @Operation(summary = "Returns the count of books")
    @APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON))
    @APIResponse(responseCode = "503", description = "Service not available")
    @GET
    @Path("/count")
    public JsonObject count() {
        return Json.createObjectBuilder().add("book.count", service.count()).build();
    }

    @Operation(summary = "Returns a random book")
    @APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Book.class)))
    @APIResponse(responseCode = "503", description = "Service not available")
    @GET
    @Path("/random")
    public Response getRandomBook() {
        Book book = service.findRandomBook();
        return Response.ok(book).build();
    }

    @Operation(summary = "Returns a book for a given identifier")
    @APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Book.class)))
    @APIResponse(responseCode = "404", description = "The book is not found for the given identifier")
    @APIResponse(responseCode = "503", description = "Service not available")
    @GET
    @Path("/{id}")
    public Response getBook(@Parameter(description = "Book identifier", required = true) @PathParam("id") Long id) {
        Optional<Book> book = service.findBookById(id);
        if (book.isPresent()) {
            return Response.ok(book).build();
        } else {
            return Response.status(NOT_FOUND).build();
        }
    }

    @Operation(summary = "Creates a valid book")
    @APIResponse(responseCode = "201", description = "The URI of the created book", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = URI.class)))
    @APIResponse(responseCode = "202", description = "The book has not been yet created but will be ASAP")
    @APIResponse(responseCode = "503", description = "Service not available")
    @POST
    @Timed(name = "created_book_time", description = "Times how long it takes to invoke the createBook method", unit = MetricUnits.MILLISECONDS, absolute = true, tags = {"version=tnt-2022", "author=jefrajames"})
    public Response createBook(
            @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Book.class))) @Valid Book book,
            @Context UriInfo uriInfo) {

        try {
            book = service.registerBook(book);
            UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(Long.toString(book.id));
            URI uri = builder.build();
            return Response.created(uri).build();
        } catch (IllegalStateException ex) {
            // REST number not accessible
            log.warning("IllegalStateException when registering book: " + ex.getMessage());
            return Response.status(ACCEPTED).build();
        } catch (DuplicateBookException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ex.getMessage())
                    .build();
        }

    }

    @Operation(summary = "Updates an existing book")
    @APIResponse(responseCode = "200", description = "The updated book", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Book.class)))
    @APIResponse(responseCode = "503", description = "Service not available")
    @PUT
    // @Counted(name = "countUpdateBook", description = "Counts how many times the updateBook method has been invoked", absolute = true, tags = {"version=demo", "author=JFJ"})
    // @Timed(name = "timeUpdateBook", description = "Times how long it takes to invoke the updateBook method", unit = MetricUnits.MILLISECONDS, absolute = true, tags = {"version=demo", "author=JFJ"})
    public Response updateBook(
            @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Book.class))) @Valid Book book) {
        book = service.updateBook(book);
        return Response.ok(book).build();
    }

    @Operation(summary = "Deletes an existing book")
    @APIResponse(responseCode = "204", description = "The book has been successfully deleted")
    @APIResponse(responseCode = "503", description = "Service not available")
    @DELETE
    @Path("/{id}")
    public Response deleteBook(@Parameter(description = "Book identifier", required = true) @PathParam("id") Long id) {
        service.deleteBook(id);
        log.info("Book deleted with " + id);
        return Response.noContent().build();
    }

    @Operation(summary = "List books pending for creation")
    @APIResponse(responseCode = "200", description = "The list of pending books")
    @APIResponse(responseCode = "503", description = "Service not available")
    @GET
    @Path("/pending")
    public JsonArray findPending() {
        return service.findPendingBooks();
    }

}