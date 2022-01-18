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

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.ExternalDocumentation;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 *
 * @author jefrajames
 */
@ApplicationPath("/api/numbers")
@OpenAPIDefinition(
        info = @Info(title = "Number Book API",
                description = "This API allows to generate all sorts of numbers",
                version = "1.0",
                contact = @Contact(name = "@jefrajames", url = "https://twitter.com/jefrajames")),
        externalDocs = @ExternalDocumentation(url = "https://github.com/jefrajames", description = "Bookstore demo"),
        tags = {
            @Tag(name = "api", description = "Public API that can be used by anybody"),
            @Tag(name = "numbers", description = "Anybody interested in numbers")
        }
)
public class RestApplication extends Application { }
