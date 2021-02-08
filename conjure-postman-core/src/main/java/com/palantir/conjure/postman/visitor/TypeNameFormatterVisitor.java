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

import com.palantir.conjure.spec.AliasDefinition;
import com.palantir.conjure.spec.EnumDefinition;
import com.palantir.conjure.spec.ExternalReference;
import com.palantir.conjure.spec.ListType;
import com.palantir.conjure.spec.MapType;
import com.palantir.conjure.spec.ObjectDefinition;
import com.palantir.conjure.spec.OptionalType;
import com.palantir.conjure.spec.PrimitiveType;
import com.palantir.conjure.spec.SetType;
import com.palantir.conjure.spec.Type;
import com.palantir.conjure.spec.TypeDefinition;
import com.palantir.conjure.spec.TypeName;
import com.palantir.conjure.spec.UnionDefinition;
import com.palantir.conjure.visitor.TypeDefinitionVisitor;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class TypeNameFormatterVisitor implements Type.Visitor<String> {

    private final Map<TypeName, TypeDefinition> types;

    public TypeNameFormatterVisitor(List<TypeDefinition> types) {
        this(types.stream()
                .collect(Collectors.toMap(type -> type.accept(TypeDefinitionVisitor.TYPE_NAME), Function.identity())));
    }

    private TypeNameFormatterVisitor(Map<TypeName, TypeDefinition> types) {
        this.types = types;
    }

    @Override
    public String visitPrimitive(PrimitiveType value) {
        return value.get().name();
    }

    @Override
    public String visitOptional(OptionalType value) {
        return String.format("Optional<%s>", value.getItemType().accept(this));
    }

    @Override
    public String visitList(ListType value) {
        return String.format("List<%s>", value.getItemType().accept(this));
    }

    @Override
    public String visitSet(SetType value) {
        return String.format("Set<%s>", value.getItemType().accept(this));
    }

    @Override
    public String visitMap(MapType value) {
        return String.format("Map<%s, %s>", value.getKeyType().accept(this), value.getValueType().accept(this));
    }

    @Override
    public String visitReference(TypeName value) {
        TypeDefinition definition = types.get(value);
        TypeNameFormatterVisitor visitor = this;
        return definition.accept(new TypeDefinition.Visitor<String>() {

            @Override
            public String visitAlias(AliasDefinition value) {
                return String.format("%s(%s)", value.getTypeName().getName(), value.getAlias().accept(visitor));
            }

            @Override
            public String visitEnum(EnumDefinition value) {
                return value.getTypeName().getName();
            }

            @Override
            public String visitObject(ObjectDefinition value) {
                return value.getTypeName().getName();
            }

            @Override
            public String visitUnion(UnionDefinition value) {
                return value.getTypeName().getName();
            }

            @Override
            public String visitUnknown(String _unknownType) {
                return "UNKNOWN";
            }
        });
    }

    @Override
    public String visitExternal(ExternalReference value) {
        return value.getExternalReference().getName();
    }

    @Override
    public String visitUnknown(String _unknownType) {
        return "UNKNOWN";
    }
}
