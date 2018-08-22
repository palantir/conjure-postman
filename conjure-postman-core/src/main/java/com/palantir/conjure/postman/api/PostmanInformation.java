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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.palantir.tokens.auth.ImmutablesStyle;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutablePostmanInformation.class)
@JsonSerialize(as = ImmutablePostmanInformation.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@ImmutablesStyle
public interface PostmanInformation {

    @Value.Parameter
    @JsonProperty
    Optional<String> description();

    @Value.Parameter
    String name();

    @Value.Parameter
    @JsonProperty("_postman_id")
    String postmanId();

    @Value.Parameter
    String schema();

    @Value.Parameter
    String version();

    static Builder builder() {
        return new Builder();
    }

    class Builder extends ImmutablePostmanInformation.Builder {}
}
