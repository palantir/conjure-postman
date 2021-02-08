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

package com.palantir.conjure.postman.visitor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.palantir.conjure.postman.api.PostmanRequest;
import com.palantir.conjure.spec.ArgumentDefinition;
import com.palantir.conjure.spec.BodyParameterType;
import com.palantir.conjure.spec.ExternalReference;
import com.palantir.conjure.spec.ListType;
import com.palantir.conjure.spec.MapType;
import com.palantir.conjure.spec.OptionalType;
import com.palantir.conjure.spec.PrimitiveType;
import com.palantir.conjure.spec.SetType;
import com.palantir.conjure.spec.Type;
import com.palantir.conjure.spec.TypeDefinition;
import com.palantir.conjure.spec.TypeName;
import java.util.List;
import java.util.Optional;

public final class BodyParameterTypeVisitor extends DefaultParameterTypeVisitor<Optional<PostmanRequest.Body>> {

    private final ArgumentDefinition argumentDefinition;
    private final List<TypeDefinition> types;

    public BodyParameterTypeVisitor(ArgumentDefinition argumentDefinition, List<TypeDefinition> types) {
        super(Optional.empty());
        this.argumentDefinition = argumentDefinition;
        this.types = types;
    }

    @Override
    public Optional<PostmanRequest.Body> visitBody(BodyParameterType _value) {
        TemplateTypeVisitor visitor = new TemplateTypeVisitor(types);
        Type type = argumentDefinition.getType();

        return type.accept(new Type.Visitor<Optional<PostmanRequest.Body>>() {
            @Override
            public Optional<PostmanRequest.Body> visitPrimitive(PrimitiveType value) {
                switch (value.get()) {
                    case BINARY:
                        return Optional.of(PostmanRequest.FileBody.builder().build());
                    default:
                        return rawBody(visitor.visitPrimitive(value));
                }
            }

            @Override
            public Optional<PostmanRequest.Body> visitOptional(OptionalType value) {
                return rawBody(visitor.visitOptional(value));
            }

            @Override
            public Optional<PostmanRequest.Body> visitList(ListType value) {
                return rawBody(visitor.visitList(value));
            }

            @Override
            public Optional<PostmanRequest.Body> visitSet(SetType value) {
                return rawBody(visitor.visitSet(value));
            }

            @Override
            public Optional<PostmanRequest.Body> visitMap(MapType value) {
                return rawBody(visitor.visitMap(value));
            }

            @Override
            public Optional<PostmanRequest.Body> visitReference(TypeName value) {
                return rawBody(visitor.visitReference(value));
            }

            @Override
            public Optional<PostmanRequest.Body> visitExternal(ExternalReference value) {
                return rawBody(visitor.visitExternal(value));
            }

            @Override
            public Optional<PostmanRequest.Body> visitUnknown(String _unknownType) {
                return Optional.empty();
            }
        });
    }

    private static Optional<PostmanRequest.Body> rawBody(JsonNode content) {
        try {
            String serializedContent = TemplateTypeVisitor.getObjectMapper().writeValueAsString(content);
            return Optional.of(PostmanRequest.RawBody.builder().raw(serializedContent).build());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
