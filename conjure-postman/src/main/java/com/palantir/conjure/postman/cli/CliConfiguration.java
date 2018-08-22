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

package com.palantir.conjure.postman.cli;

import com.google.common.base.Preconditions;
import java.io.File;
import java.util.Optional;
import org.apache.commons.cli.Option;
import org.immutables.value.Value;

@Value.Immutable
public abstract class CliConfiguration {
    public static final String PRODUCT_NAME = "productName";
    public static final String PRODUCT_DESCRIPTION = "productDescription";
    public static final String PRODUCT_VERSION = "productVersion";
    public static final String API_PATH = "apiPath";

    abstract File target();

    abstract File outputDirectory();

    abstract String productName();

    abstract String productVersion();

    abstract Optional<String> productDescription();

    abstract Optional<String> apiPath();

    @Value.Check
    final void check() {
        Preconditions.checkArgument(target().isFile(), "Target must exist and be a file");
        Preconditions.checkArgument(outputDirectory().isDirectory(), "Output must exist and be a directory");
    }

    static Builder builder() {
        return new Builder();
    }

    static CliConfiguration of(String target, String outputDirectory, Option[] options) {
        Builder builder = new Builder()
                .target(new File(target))
                .outputDirectory(new File(outputDirectory));
        for (Option option : options) {
            switch (option.getLongOpt()) {
                case PRODUCT_NAME:
                    builder.productName(option.getValue());
                    break;
                case PRODUCT_VERSION:
                    builder.productVersion(option.getValue());
                    break;
                case PRODUCT_DESCRIPTION:
                    builder.productDescription(option.getValue());
                    break;
                case API_PATH:
                    builder.apiPath(option.getValue());
                    break;
                default:
                    break;
            }
        }

        return builder.build();
    }

    public static final class Builder extends ImmutableCliConfiguration.Builder {}
}
