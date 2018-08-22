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

package com.palantir.conjure.postman.writer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.palantir.conjure.postman.api.PostmanCollection;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public final class DefaultPostmanCollectionFileWriter implements PostmanCollectionFileWriter {

    private static final ObjectMapper mapper = new ObjectMapper()
                .registerModule(new Jdk8Module())
                .enable(SerializationFeature.INDENT_OUTPUT);
    private final Path basePath;

    public DefaultPostmanCollectionFileWriter(Path basePath) {
        this.basePath = basePath;
    }

    @Override
    public void write(PostmanCollection collection) {
        Path filePath = basePath.resolve(PostmanCollectionFileWriter.getPath(collection));
        try {
            Files.createDirectories(filePath.getParent());
            try (OutputStream os = Files.newOutputStream(filePath)) {
                mapper.writeValue(os, collection);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
