package io.jefrajames.bookstore.book.client;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.jefrajames.bookstore.book.boundary.BookResourceTest;
import io.jefrajames.bookstore.book.control.numbers.IsbnNumbers;
import io.jefrajames.bookstore.book.control.numbers.NumberClient;
import io.quarkus.test.Mock;
import lombok.extern.java.Log;

@ApplicationScoped
@RestClient
@Mock
@Log
public class MockNumberProxy implements NumberClient {

    @Override
    public IsbnNumbers generateIsbnNumbers() {
        log.info("calling MockNumberProxy");

        IsbnNumbers isbnNumbers = new IsbnNumbers(); 
        isbnNumbers.setIsbn13(BookResourceTest.MOCK_ISBN_13); 
        isbnNumbers.setIsbn10(BookResourceTest.MOCK_ISBN_10);
        
        return isbnNumbers;
    }
    
}
