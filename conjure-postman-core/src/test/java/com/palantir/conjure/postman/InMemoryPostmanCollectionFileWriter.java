/*
 * (c) Copyright 2018 Palantir Technologies Inc. All rights reserved.
 */

package com.palantir.conjure.postman;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.common.collect.Maps;
import com.palantir.conjure.postman.api.PostmanCollection;
import com.palantir.conjure.postman.writer.PostmanCollectionFileWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;

public final class InMemoryPostmanCollectionFileWriter implements PostmanCollectionFileWriter {
    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new Jdk8Module())
            .enable(SerializationFeature.INDENT_OUTPUT);

    private final Map<Path, String> collections = Maps.newHashMap();

    public Map<Path, String> getCollections() {
        return collections;
    }

    @Override
    public void write(PostmanCollection collection) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            mapper.writeValue(baos, collection);
            byte[] bytes = baos.toByteArray();

            collections.put(PostmanCollectionFileWriter.getPath(collection), new String(bytes, StandardCharsets.UTF_8));
        } catch (IOException  e) {
            throw new RuntimeException(e);
        }
    }
}
