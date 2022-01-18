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

/**
 *
 * @author jefrajames
 */
public class DuplicateBookException extends Exception {

    /**
     * Creates a new instance of <code>DuplicateBookException</code> without
     * detail message.
     */
    public DuplicateBookException() {
    }

    /**
     * Constructs an instance of <code>DuplicateBookException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public DuplicateBookException(String msg) {
        super(msg);
    }
}
