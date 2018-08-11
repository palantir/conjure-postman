/*
 * (c) Copyright 2018 Palantir Technologies Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.palantir.conjure.postman;

import com.google.common.collect.ImmutableList;
import com.google.common.net.HttpHeaders;
import com.palantir.conjure.postman.api.PostmanRequest;
import com.palantir.conjure.postman.api.PostmanUrl;
import com.palantir.conjure.postman.visitor.BodyParameterTypeVisitor;
import com.palantir.conjure.postman.visitor.DefaultParameterTypeVisitor;
import com.palantir.conjure.spec.Documentation;
import com.palantir.conjure.spec.EndpointDefinition;
import com.palantir.conjure.spec.HeaderParameterType;
import com.palantir.conjure.spec.TypeDefinition;
import com.palantir.conjure.visitor.ParameterTypeVisitor;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.ws.rs.core.MediaType;

public final class PostmanRequestGenerator {

    private static final ImmutableList<PostmanRequest.Header> DEFAULT_HEADERS =
            ImmutableList.of(PostmanRequest.Header.of(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));

    PostmanRequestGenerator() {}

    public PostmanRequest generateRequest(
            String apiBaseVariable,
            List<TypeDefinition> types,
            EndpointDefinition endpointDefinition) {

        PostmanRequest.Request request = PostmanRequest.Request.builder()
                .url(getUrl(apiBaseVariable, endpointDefinition))
                .method(getMethod(endpointDefinition))
                .header(getHeaders(endpointDefinition))
                .description(getDocs(endpointDefinition))
                .body(getBody(endpointDefinition, types))
                .build();

        return PostmanRequest.builder()
                .name(getName(endpointDefinition))
                .description(getDocs(endpointDefinition))
                .request(request)
                .build();
    }

    private static String getName(EndpointDefinition endpointDefinition) {
        StringBuilder name = new StringBuilder();
        name.append(endpointDefinition.getEndpointName().get());
        endpointDefinition.getDeprecated().ifPresent(documentation -> name.append(" (Deprecated)"));
        return name.toString();
    }

    private static Optional<String> getDocs(EndpointDefinition endpointDefinition) {
        StringBuilder docs = new StringBuilder();
        endpointDefinition.getDocs().ifPresent(documentation -> docs.append(documentation.get()));
        endpointDefinition.getDeprecated().ifPresent(documentation ->
                docs.append(String.format("\n\n**Deprecation:** %s", documentation.get())));
        endpointDefinition.getReturns().ifPresent(type ->
                docs.append(String.format("\n\n**Returns:** {{%s}}", type)));
        return docs.length() > 0 ? Optional.of(docs.toString()) : Optional.empty();
    }

    private static PostmanRequest.Request.Method getMethod(EndpointDefinition endpointDefinition) {
        return PostmanRequest.Request.Method.valueOf(endpointDefinition.getHttpMethod().toString());
    }

    /**
     * Given a {@code contextPath} and {@link EndpointDefinition}, returns the fully-qualified path to the endpoint.
     * Path parameters are converted to Postman variables.
     */
    private static String getPath(String apiBaseVariable, EndpointDefinition endpointDefinition) {
        String path = Paths.get(apiBaseVariable).resolve(endpointDefinition.getHttpPath().get()).toString();
        return path.replaceAll("\\{", ":").replaceAll("}", "");
    }

    /**
     * Coverts a {@code path} and {@code query} to a URL. The hostname is stored as the
     * {@link PostmanUrl#HOSTNAME_VARIABLE} Postman pathParam.
     */
    private static PostmanUrl getUrl(String apiBaseVariable, EndpointDefinition endpointDefinition) {
        String path = getPath(apiBaseVariable, endpointDefinition);
        String raw = String.format("%s:%s/%s%s",
                PostmanUrl.HOSTNAME_VARIABLE, PostmanUrl.PORT_VARIABLE, apiBaseVariable, path);


        PostmanUrl.Builder url = PostmanUrl.builder()
                .raw(raw);

        url.addAllPathParam(endpointDefinition.getArgs().stream()
                .filter(arg -> arg.getParamType().accept(ParameterTypeVisitor.IS_PATH))
                .map(arg -> PostmanUrl.Variable.builder()
                        .key(arg.getArgName().get())
                        .description(arg.getDocs().map(Documentation::get))
                        .build())
                .collect(Collectors.toList()));

        url.addAllQueryParam(endpointDefinition.getArgs().stream()
                .filter(arg -> arg.getParamType().accept(ParameterTypeVisitor.IS_QUERY))
                .map(arg -> PostmanUrl.QueryParam.builder()
                        .key(arg.getArgName().get())
                        .build())
                .collect(Collectors.toList()));

        return url.build();
    }

    /**
     * Given an {@link EndpointDefinition}, returns the list of headers. All
     * {@link PostmanRequestGenerator#DEFAULT_HEADERS} will be added.
     */
    private static List<PostmanRequest.Header> getHeaders(EndpointDefinition endpointDefinition) {
        Stream<PostmanRequest.Header> endpointHeaders = endpointDefinition.getArgs().stream()
                .map(argumentDefinition -> argumentDefinition.getParamType()
                        .accept(new DefaultParameterTypeVisitor<Optional<PostmanRequest.Header>>(Optional.empty()) {
                            @Override
                            public Optional<PostmanRequest.Header> visitHeader(HeaderParameterType value) {
                                PostmanRequest.Header header = PostmanRequest.Header.builder()
                                        .key(value.getParamId().get())
                                        .description(argumentDefinition.getDocs().map(Documentation::get))
                                        .build();
                                return Optional.of(header);
                            }
                        })
                )
                .filter(Optional::isPresent)
                .map(Optional::get);
        return Stream.concat(DEFAULT_HEADERS.stream(), endpointHeaders)
                .sorted(Comparator.comparing(PostmanRequest.Header::key))
                .collect(Collectors.toList());
    }

    private static Optional<PostmanRequest.Body> getBody(
            EndpointDefinition endpointDefinition,
            List<TypeDefinition> types) {
        return endpointDefinition.getArgs().stream()
                .map(argumentDefinition -> argumentDefinition.getParamType()
                        .accept(new BodyParameterTypeVisitor(argumentDefinition, types)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }
}
