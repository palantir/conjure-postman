/*
 * (c) Copyright 2018 Palantir Technologies Inc. All rights reserved.
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
                        .build()));
    }

    static Builder builder() {
        return new Builder();
    }

    class Builder extends ImmutablePostmanAuth.Builder {}

}
