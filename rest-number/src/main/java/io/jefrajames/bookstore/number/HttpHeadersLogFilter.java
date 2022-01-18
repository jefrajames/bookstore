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

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

/**
 * A JAX-RS filter that display HTTP headers.
 * 
 * Demonstrates the use of uber-trace-id used by Jaeger
 * 
 * @author jefrajames
 */
@Provider
public class HttpHeadersLogFilter implements ContainerRequestFilter {

    // uber-trace-id used by Jaeger to associate spans
    private void printHeaderInfo(MultivaluedMap<String, String> headers) {
        System.out.println("--------------- HTTP Headers ---------------");
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }
    }

    @Override
    public void filter(ContainerRequestContext context) throws IOException {
        printHeaderInfo(context.getHeaders());
    }

}
