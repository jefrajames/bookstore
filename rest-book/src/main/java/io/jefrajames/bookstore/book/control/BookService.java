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
package io.jefrajames.bookstore.book.control;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.bind.JsonbBuilder;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.MetricType;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.Tag;
import org.eclipse.microprofile.metrics.annotation.Gauge;
import org.eclipse.microprofile.opentracing.Traced;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.jefrajames.bookstore.book.control.numbers.IsbnNumbers;
import io.jefrajames.bookstore.book.control.numbers.NumberClient;
import io.jefrajames.bookstore.book.entity.Book;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.extern.java.Log;

/**
 * Business logic related to the Book entity.
 *
 * Makes use of Panache, RestClient and FaultTolerance (fallback).
 *
 * This class is traced by OpenAPI thanks to the @Traced annotation
 *
 * @author jefrajames
 */
@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
@Traced
@RegisterForReflection
@Log
public class BookService {

    @RestClient
    @Inject
    NumberClient numberClient;

    @Inject
    MetricRegistry mr;

    Counter bookDuplicationCount;

    @PostConstruct
    void postConstruct() {

        Metadata md = Metadata
                .builder()
                .withName("book-duplication-count")
                .withDescription("How many book duplications")
                .withType(MetricType.COUNTER)
                .build();

        bookDuplicationCount = mr.counter(md, new Tag("demo", "tnt-2022"), new Tag("author", "jefrajames"));
    }

    // Check same author and same title
    private void checkDuplicateBook(Book book) throws DuplicateBookException {

        if (Book.count("author =?1 and title=?2", book.author, book.title) != 0) {
            bookDuplicationCount.inc();
            throw new DuplicateBookException("Book " + book.title + " from " + book.author + " already registered");
        }

    }

    @Fallback(fallbackMethod = "fallbackPersistBook", skipOn = { DuplicateBookException.class })
    public Book registerBook(@Valid Book book) throws DuplicateBookException {

        // Check if book not already registered
        checkDuplicateBook(book);

        // Check error case when Service Number no accessible
        IsbnNumbers isbnNumbers = numberClient.generateIsbnNumbers();
        book.isbn13 = isbnNumbers.getIsbn13();
        book.isbn10 = isbnNumbers.getIsbn10();

        Book.persist(book);
        return book;
    }

    // We have no ISBN numbers, we cannot persist in the database
    private Book fallbackPersistBook(Book book) throws FileNotFoundException {
        // Book data is stored in a flat file for later batch processing
        String bookJson = JsonbBuilder.create().toJson(book);
        try (PrintWriter out = new PrintWriter("book-" + Instant.now().toEpochMilli() + ".json")) {
            out.println(bookJson);
        }

        throw new IllegalStateException("Numbers service not accessible");
    }

    public JsonArray findPendingBooks() {

        var dir = new File(".");
        File[] bookFiles = dir.listFiles((d, name) -> name.startsWith("book-"));
        var builder = Json.createArrayBuilder();
        for (File pending : bookFiles) {
            try ( var jsonReader = Json.createReader(new FileReader(pending))) {
                builder.add(jsonReader.readObject());
            } catch (FileNotFoundException ex) {
                log.warning("findPendingBooks NOK " + ex.getMessage());
            }
        }

        return builder.build();
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Book> findAllBooks() {
        return Book.listAll();
    }

    @Gauge(name = "total_book_count", description = "Total count of books", unit = MetricUnits.NONE, absolute = true, tags = {
            "version=tnt-2022", "author=jefrajames" })
    public long count() {
        return Book.count();
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public Optional<Book> findBookById(Long id) {
        return Book.findByIdOptional(id);
    }

    // To be tested
    public Book updateBook(@Valid Book book) {
        book.persist();
        return book;
    }

    // To be tested
    public void deleteBook(Long id) {
        Book.deleteById(id);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public Book findRandomBook() {
        Book randomBook = null;
        while (randomBook == null) {
            randomBook = Book.findRandom();
        }
        return randomBook;
    }

}
