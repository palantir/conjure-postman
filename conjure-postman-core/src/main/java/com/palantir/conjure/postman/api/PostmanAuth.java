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
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonPropertyOrder(alphabetic = true)
@JsonDeserialize(as = ImmutablePostmanAuth.class)
@JsonSerialize(as = ImmutablePostmanAuth.class)
public interface PostmanAuth {

    String AUTH_VARIABLE = "{{AUTH_TOKEN}}";

    @JsonProperty
    default String type() {
        return "bearer";
    }

    @JsonProperty
    default Optional<List<Map<String, String>>> bearer() {
        return Optional.of(ImmutableList.of(ImmutableMap.<String, String>builder()
                .put("key", "token")
                .put("value", AUTH_VARIABLE)
                .put("type", "string")
                .buildOrThrow()));
    }

    static Builder builder() {
        return new Builder();
    }

    class Builder extends ImmutablePostmanAuth.Builder {}
}
