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

import com.palantir.conjure.postman.api.PostmanCollection;
import java.nio.file.Path;
import java.nio.file.Paths;

public interface PostmanCollectionFileWriter {

    void write(PostmanCollection collection);

    @SuppressWarnings("for-rollout:StringCaseLocaleUsage")
    static Path getPath(PostmanCollection collection) {
        return Paths.get(String.format(
                "%s.postman_collection.json",
                collection.info().name().replaceAll(" ", "-").toLowerCase()));
    }
}
