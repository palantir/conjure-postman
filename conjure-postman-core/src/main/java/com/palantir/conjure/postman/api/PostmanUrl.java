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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutablePostmanUrl.class)
@JsonSerialize(as = ImmutablePostmanUrl.class)
public interface PostmanUrl {
    String HOSTNAME_VARIABLE = "{{HOSTNAME}}";
    String PORT_VARIABLE = "{{PORT}}";
    String API_BASE_VARIABLE = "{{%s_API_BASE}}";

    static String formatApiBase(String productName) {
        return String.format(API_BASE_VARIABLE, productName.replace(" ", "_").toUpperCase());
    }

    @JsonProperty
    String raw();

    @JsonProperty
    default String host() {
        return HOSTNAME_VARIABLE;
    }

    @JsonProperty
    default String port() {
        return PORT_VARIABLE;
    }

    @JsonProperty
    default List<String> path() {
        return ImmutableList.copyOf(raw().replace("{{HOSTNAME}}:{{PORT}}/", "").split("/"));
    }

    @JsonProperty("variable")
    List<Variable> pathParam();

    @JsonProperty("query")
    List<QueryParam> queryParam();

    @Value.Immutable
    @JsonDeserialize(as = ImmutableVariable.class)
    @JsonSerialize(as = ImmutableVariable.class)
    interface Variable {

        @JsonProperty
        String key();

        @JsonProperty
        Optional<String> description();

        static Variable.Builder builder() {
            return new Variable.Builder();
        }

        class Builder extends ImmutableVariable.Builder {}
    }

    @Value.Immutable
    @JsonDeserialize(as = ImmutableQueryParam.class)
    @JsonSerialize(as = ImmutableQueryParam.class)
    interface QueryParam {

        @JsonProperty
        String key();

        @Value.Default
        @JsonProperty
        default String value() {
            return "";
        }

        @Value.Default
        @JsonProperty
        default boolean disabled() {
            return false;
        }

        static QueryParam.Builder builder() {
            return new QueryParam.Builder();
        }

        class Builder extends ImmutableQueryParam.Builder {}
    }

    static Builder builder() {
        return new Builder();
    }

    class Builder extends ImmutablePostmanUrl.Builder {}
}
