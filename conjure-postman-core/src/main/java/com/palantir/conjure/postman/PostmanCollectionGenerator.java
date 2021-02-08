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

import com.palantir.conjure.postman.api.PostmanAuth;
import com.palantir.conjure.postman.api.PostmanCollection;
import com.palantir.conjure.postman.api.PostmanEvent;
import com.palantir.conjure.postman.api.PostmanFolder;
import com.palantir.conjure.postman.api.PostmanInformation;
import com.palantir.conjure.postman.api.PostmanUrl;
import com.palantir.conjure.postman.api.PostmanVariable;
import com.palantir.conjure.spec.ConjureDefinition;
import com.palantir.conjure.spec.Documentation;
import com.palantir.conjure.spec.TypeDefinition;
import java.util.List;
import java.util.stream.Collectors;

public final class PostmanCollectionGenerator {

    private static final String POSTMAN_SCHEMA = "https://schema.getpostman.com/json/collection/v2.1.0/collection.json";

    private GeneratorConfiguration config;

    public PostmanCollectionGenerator(GeneratorConfiguration config) {
        this.config = config;
    }

    public PostmanCollection generate(ConjureDefinition conjureDefinition) {

        PostmanCollection.Builder collection = PostmanCollection.builder();

        String productDescription = String.format("# %s %s", config.productName(), config.productVersion());
        if (config.productDescription().isPresent()) {
            productDescription = productDescription.concat(
                    String.format("\n\n%s", config.productDescription().get()));
        }

        collection.info(PostmanInformation.builder()
                .name(config.productName())
                .version(config.productVersion())
                .description(productDescription)
                .postmanId(UuidProvider.getUuid().toString())
                .schema(POSTMAN_SCHEMA)
                .build());

        collection.auth(PostmanAuth.builder().build());

        collection.addEvent(PostmanEvent.Test.of(PostmanEvent.Script.NON_ERROR_STATUS));
        collection.addEvent(PostmanEvent.Test.of(PostmanEvent.Script.RESPONSE_IS_JSON));

        collection.addVariable(PostmanVariable.builder()
                .key("PORT")
                .name("PORT")
                .value(443)
                .type(PostmanVariable.Type.NUMBER)
                .build());

        String formattedApiBaseVariable = PostmanUrl.formatApiBase(config.productName());
        String formattedApiBaseName = formattedApiBaseVariable.replaceAll("[{}]", "");
        config.apiPath()
                .ifPresent(apiPath -> collection.addVariable(PostmanVariable.builder()
                        .key(formattedApiBaseName)
                        .name(formattedApiBaseName)
                        .value(apiPath)
                        .type(PostmanVariable.Type.STRING)
                        .build()));

        List<TypeDefinition> allTypes = conjureDefinition.getTypes();
        PostmanRequestGenerator requestGenerator = new PostmanRequestGenerator();
        collection.addAllItems(conjureDefinition.getServices().stream()
                .map(service -> {
                    PostmanFolder.Builder folder = PostmanFolder.builder();
                    folder.name(service.getServiceName().getName());
                    folder.description(service.getDocs().map(Documentation::get));
                    folder.addAllItems(service.getEndpoints().stream()
                            .map(endpoint ->
                                    requestGenerator.generateRequest(formattedApiBaseVariable, allTypes, endpoint))
                            .collect(Collectors.toList()));
                    return folder.build();
                })
                .collect(Collectors.toList()));

        return collection.build();
    }
}
