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

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import lombok.Data;
import lombok.ToString;

/**
 *
 * @author jefrajames
 */
@Data
@ToString(includeFieldNames = true)
@Schema(description = "Several kinds of book numbers")
public class BookNumberResponse {
    
    @JsonbProperty("isbn_10")
    private String isbn10;
    
    @JsonbProperty("isbn_13")
    private String isbn13;
   
    private String asin;
    
    @JsonbProperty("ean_8")
    private String ean8;
    
    @JsonbProperty("ean_13")
    private String ean13;
    
    @JsonbTransient
    // @JsonbProperty("generated_at")
    // Default format is: 2021-12-23T10:54:43.045048Z
    private Instant generationDate;
}
