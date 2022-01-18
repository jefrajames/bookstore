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
package io.jefrajames.bookstore.book.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Random;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Main business entity.
 * 
 * @author jefrajames
 */
@Schema(description = "Book representation")
@Entity
public class Book extends PanacheEntity {

    @NotBlank
    @Schema(required = true)
    public String title;
    
    @Column(name = "isbn_13")
    public String isbn13;
    
    @Column(name = "isbn_10")
    public String isbn10;
    
    @NotBlank
    public String author;
    
    @NotNull
    @Column(name = "year_of_publication")
    public Integer yearOfPublication;
    
    @Column(name = "nb_of_pages")
    public Integer nbOfPages;
    
    @Min(1) @Max(10)
    public Integer rank;
    
    public BigDecimal price;
    
    @Column(name = "small_image_url")
    public URL smallImageUrl;
    
    @Column(name = "medium_image_url")
    public URL mediumImageUrl;
    
    @Column(length = 10000)
    @Size(min = 1, max = 10000)
    public String description;

    public static Book findRandom() {
        long countBooks = Book.count();
        int randomBook = new Random().nextInt((int) countBooks);
        return Book.findAll().page(randomBook, 1).firstResult();
    }

}
