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
package io.jefrajames.bookstore.maintenance.boundary;

import java.io.IOException;
import java.time.LocalDateTime;

import javax.enterprise.event.Observes;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.microprofileext.config.event.ChangeEvent;

import lombok.Getter;
import lombok.extern.java.Log;

/**
 * Filters resource classes execution if the site is in maintenance.
 * 
 * Bind to resource classes annotated with @CheckMaintenance
 * 
 * @author jefrajames
 */
@CheckMaintenance
@Provider
@Log
public class CheckMaintenanceFilter implements ContainerRequestFilter {

    @Getter
    @ConfigProperty(name="app.is.open")
    boolean appIsOpen;

    @Getter
    LocalDateTime appIsOpenChangedAt = LocalDateTime.now();

    // Change in memory source config
    void observeChangeConfigEvent(@Observes ChangeEvent changeEvent) {

        if ( "app.is.open".equals(changeEvent.getKey()) ) {
            if ( "false".equals(changeEvent.getNewValue()) || "true".equals(changeEvent.getNewValue()) ) {
                appIsOpen = Boolean.valueOf(changeEvent.getNewValue());
                appIsOpenChangedAt = LocalDateTime.now();
            }    
            else 
                log.warning("app.is.open value should be false or true");  
        }

    }

    public void filter(ContainerRequestContext context) throws IOException {

        if ( !appIsOpen ) {
            log.warning(("SERVICE_UNAVAILABLE, site in maintenance"));
            context.abortWith(
                    Response.status(Response.Status.SERVICE_UNAVAILABLE)
                            .entity("{\"reason\":\"Service currently in maintenance\"}")
                            .build()
            );
        }

    }
    
}
