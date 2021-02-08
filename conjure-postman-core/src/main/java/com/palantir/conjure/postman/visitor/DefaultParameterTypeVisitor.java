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

package com.palantir.conjure.postman.visitor;

import com.palantir.conjure.spec.BodyParameterType;
import com.palantir.conjure.spec.HeaderParameterType;
import com.palantir.conjure.spec.ParameterType;
import com.palantir.conjure.spec.PathParameterType;
import com.palantir.conjure.spec.QueryParameterType;

public abstract class DefaultParameterTypeVisitor<T> implements ParameterType.Visitor<T> {

    private final T defaultValue;

    protected DefaultParameterTypeVisitor(T defaultValue) {
        this.defaultValue = defaultValue;
    }

    @SuppressWarnings("checkstyle:designforextension")
    @Override
    public T visitBody(BodyParameterType _value) {
        return defaultValue;
    }

    @SuppressWarnings("checkstyle:designforextension")
    @Override
    public T visitHeader(HeaderParameterType _value) {
        return defaultValue;
    }

    @SuppressWarnings("checkstyle:designforextension")
    @Override
    public T visitPath(PathParameterType _value) {
        return defaultValue;
    }

    @SuppressWarnings("checkstyle:designforextension")
    @Override
    public T visitQuery(QueryParameterType _value) {
        return defaultValue;
    }

    @SuppressWarnings("checkstyle:designforextension")
    @Override
    public T visitUnknown(String _unknownType) {
        return defaultValue;
    }
}
