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
import com.google.common.collect.ImmutableList;
import com.palantir.conjure.postman.UuidProvider;
import java.util.List;
import javax.ws.rs.core.MediaType;
import org.immutables.value.Value;

public interface PostmanEvent {

    @Value.Default
    @JsonProperty
    default String id() {
        return UuidProvider.getUuid().toString();
    }

    enum Listen {
        PREREQUEST, TEST;

        @JsonValue
        public String asString() {
            return name().toLowerCase();
        }
    }

    @JsonProperty
    Listen listen();

    @JsonProperty
    Script script();

    @Value.Default
    @JsonProperty
    default boolean disabled() {
        return false;
    }

    @Value.Immutable
    @JsonDeserialize(as = ImmutableTest.class)
    @JsonSerialize(as = ImmutableTest.class)
    interface Test extends PostmanEvent {

        @Value.Derived
        @Override
        default PostmanEvent.Listen listen() {
            return Listen.TEST;
        }

        static Test of(Script script) {
            return Test.builder().script(script).build();
        }

        static Test.Builder builder() {
            return new Test.Builder();
        }

        class Builder extends ImmutableTest.Builder {}
    }

    @Value.Immutable
    @JsonDeserialize(as = ImmutablePreRequest.class)
    @JsonSerialize(as = ImmutablePreRequest.class)
    interface PreRequest extends PostmanEvent {

        @Value.Derived
        @Override
        default PostmanEvent.Listen listen() {
            return Listen.PREREQUEST;
        }

        static PreRequest of(Script script) {
            return PreRequest.builder().script(script).build();
        }

        static PreRequest.Builder builder() {
            return new PreRequest.Builder();
        }

        class Builder extends ImmutablePreRequest.Builder {}
    }

    @Value.Immutable
    interface Script {

        Script NON_ERROR_STATUS = Script.of(
                "pm.test(\"Request was successful\", function () {",
                "    pm.response.to.not.be.error;",
                "});");

        Script RESPONSE_IS_JSON = Script.of(
                "pm.test(\"Response is valid json\", function () {",
                "    // assert that the response has a valid JSON body",
                "    pm.response.to.be.json;",
                "});");

        @Value.Default
        @JsonProperty
        default String id() {
            return UuidProvider.getUuid().toString();
        }

        @Value.Default
        @JsonProperty
        default String type() {
            return MediaType.APPLICATION_JSON;
        }

        @Value.Parameter
        @JsonProperty
        List<String> exec();

        static Script of(String... lines) {
            return Script.builder().exec(ImmutableList.copyOf(lines)).build();
        }

        static Script.Builder builder() {
            return new Script.Builder();
        }

        class Builder extends ImmutableScript.Builder {}
    }
}
