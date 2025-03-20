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

package com.palantir.conjure.postman.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.palantir.conjure.postman.UuidProvider;
import java.util.List;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutablePostmanRequest.class)
@JsonSerialize(as = ImmutablePostmanRequest.class)
public interface PostmanRequest extends PostmanItem {

    @Value.Default
    @JsonProperty
    default String id() {
        return UuidProvider.getUuid().toString();
    }

    @Override
    @Value.Parameter
    @JsonProperty
    String name();

    @Value.Parameter
    Request request();

    interface Body {

        enum Mode {
            RAW,
            URLENCODED,
            FORMDATA,
            FILE;

            @SuppressWarnings("for-rollout:StringCaseLocaleUsage")
            @JsonValue
            public String asString() {
                return name().toLowerCase();
            }
        }

        @JsonProperty
        Mode mode();
    }

    @Value.Immutable
    @JsonDeserialize(as = ImmutableFileBody.class)
    @JsonSerialize(as = ImmutableFileBody.class)
    interface FileBody extends Body {

        @Value.Derived
        @Override
        default Body.Mode mode() {
            return Mode.FILE;
        }

        static Builder builder() {
            return new Builder();
        }

        class Builder extends ImmutableFileBody.Builder {}
    }

    @Value.Immutable
    @JsonDeserialize(as = ImmutableRawBody.class)
    @JsonSerialize(as = ImmutableRawBody.class)
    interface RawBody extends Body {

        @Value.Parameter
        @JsonProperty
        String raw();

        @Value.Derived
        @Override
        default Body.Mode mode() {
            return Body.Mode.RAW;
        }

        static Builder builder() {
            return new Builder();
        }

        class Builder extends ImmutableRawBody.Builder {}
    }

    @Value.Immutable
    @JsonDeserialize(as = ImmutableHeader.class)
    @JsonSerialize(as = ImmutableHeader.class)
    interface Header {

        @JsonProperty
        Optional<String> description();

        @Value.Default
        @JsonProperty
        default boolean disabled() {
            return false;
        }

        @Value.Parameter
        @JsonProperty
        String key();

        @Value.Parameter
        @JsonProperty
        Optional<String> value();

        static Header of(String key, String value) {
            return builder().key(key).value(value).build();
        }

        static Builder builder() {
            return new Builder();
        }

        class Builder extends ImmutableHeader.Builder {}
    }

    @Value.Immutable
    @JsonDeserialize(as = ImmutableRequest.class)
    @JsonSerialize(as = ImmutableRequest.class)
    interface Request {

        enum Method {
            DELETE,
            GET,
            POST,
            PUT
        }

        @JsonProperty
        Optional<Body> body();

        @JsonProperty
        Optional<String> description();

        @JsonProperty
        List<Header> header();

        @Value.Parameter
        @JsonProperty
        Method method();

        @Value.Parameter
        @JsonProperty
        PostmanUrl url();

        static Builder builder() {
            return new Builder();
        }

        class Builder extends ImmutableRequest.Builder {}
    }

    static Builder builder() {
        return new Builder();
    }

    class Builder extends ImmutablePostmanRequest.Builder {}
}
