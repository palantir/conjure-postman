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

package com.palantir.conjure.postman;

import java.util.UUID;

public abstract class UuidProvider {

    /*
     * Set to `false` for testing purposes.
     */
    private static boolean useRandom = true;

    public static void setUseRandom(boolean useRandom) {
        UuidProvider.useRandom = useRandom;
    }

    public static UUID getUuid() {
        return useRandom ? UUID.randomUUID() : new UUID(0, 0);
    }
}
