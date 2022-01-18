package io.jefrajames.bookstore;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.eclipse.microprofile.openapi.annotations.ExternalDocumentation;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * JAX-RS Application. Technically speaking this class is not useful with Quarkus.
 * 
 * In that case it enables to configure the genetic OpenAPI documentation.
 * 
 * @author jefrajames
 */
@ApplicationPath("/api")
@OpenAPIDefinition(info = @Info(title = "Book API", description = "This API allows CRUD operations on books", version = "1.0", contact = @Contact(name = "@jefrajames", url = "https://twitter.com/jefrajames")), externalDocs = @ExternalDocumentation(url = "https://github.com/jefrajames/tnt-2022", description = "TNT 2022 demo"), tags = {
                @Tag(name = "api", description = "Public API that can be used by anybody"),
                @Tag(name = "books", description = "Anybody interested in books") })

public class RestApplication extends Application {
}
