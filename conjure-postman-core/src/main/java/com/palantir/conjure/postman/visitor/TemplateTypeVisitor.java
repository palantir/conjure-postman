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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.palantir.conjure.spec.AliasDefinition;
import com.palantir.conjure.spec.EnumDefinition;
import com.palantir.conjure.spec.EnumValueDefinition;
import com.palantir.conjure.spec.ExternalReference;
import com.palantir.conjure.spec.FieldDefinition;
import com.palantir.conjure.spec.FieldName;
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
import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class TemplateTypeVisitor implements Type.Visitor<JsonNode> {

    private static final ObjectMapper objectMapper =
            new ObjectMapper().registerModule(new Jdk8Module()).enable(SerializationFeature.INDENT_OUTPUT);

    private final Map<TypeName, TypeDefinition> types;
    private final ArrayDeque<TypeName> seenTypeStack;

    public TemplateTypeVisitor(List<TypeDefinition> types) {
        this(types.stream()
                .collect(Collectors.toMap(type -> type.accept(TypeDefinitionVisitor.TYPE_NAME), Function.identity())));
    }

    private TemplateTypeVisitor(Map<TypeName, TypeDefinition> types) {
        this.types = types;
        this.seenTypeStack = new ArrayDeque<>();
    }

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    @Override
    public JsonNode visitPrimitive(PrimitiveType value) {
        return new TextNode(String.format("{{%s}}", value.get().name()));
    }

    @Override
    public JsonNode visitOptional(OptionalType value) {
        JsonNode wrapped = value.getItemType().accept(this);
        if (wrapped instanceof TextNode) {
            return new TextNode(
                    String.format("{{ Optional<%s> }}", wrapped.toString().replaceAll("[\"{}]", "")));
        }
        return wrapped;
    }

    @Override
    public JsonNode visitList(ListType value) {
        return objectMapper.createArrayNode().add(value.getItemType().accept(this));
    }

    @Override
    public JsonNode visitSet(SetType value) {
        return objectMapper.createArrayNode().add(value.getItemType().accept(this));
    }

    @Override
    public JsonNode visitMap(MapType value) {
        JsonNode keyTemplate = value.getKeyType().accept(this);
        String key = "{{KEY}}";
        if (keyTemplate instanceof TextNode) {
            key = keyTemplate.toString().replaceAll("[\"]", "");
        }
        return objectMapper.createObjectNode().set(key, value.getValueType().accept(this));
    }

    @Override
    public JsonNode visitReference(TypeName value) {
        TypeDefinition definition = types.get(value);
        TemplateTypeVisitor visitor = this;
        return definition.accept(new TypeDefinition.Visitor<JsonNode>() {

            @Override
            public JsonNode visitAlias(AliasDefinition value) {
                JsonNode wrapped = value.getAlias().accept(visitor);
                if (wrapped instanceof TextNode) {
                    return new TextNode(String.format(
                            "{{ %s(%s) }}",
                            value.getTypeName().getName(), wrapped.toString().replaceAll("[\"{}]", "")));
                }
                return wrapped;
            }

            @Override
            public JsonNode visitEnum(EnumDefinition value) {
                return new TextNode(value.getValues().stream()
                        .map(EnumValueDefinition::getValue)
                        .collect(Collectors.joining("|")));
            }

            @Override
            public JsonNode visitObject(ObjectDefinition value) {
                if (seenTypeStack.contains(value.getTypeName())) {
                    return new TextNode(
                            String.format("{{%s}}", value.getTypeName().getName()));
                }
                seenTypeStack.push(value.getTypeName());
                ObjectNode node = objectMapper.createObjectNode();
                value.getFields()
                        .forEach(fieldDefinition -> node.set(
                                fieldDefinition.getFieldName().get(),
                                fieldDefinition.getType().accept(visitor)));
                assert seenTypeStack.pop().equals(value.getTypeName());
                return node;
            }

            @Override
            public JsonNode visitUnion(UnionDefinition value) {
                if (value.getUnion().isEmpty()) {
                    return null;
                } else {
                    if (seenTypeStack.contains(value.getTypeName())) {
                        return new TextNode(
                                String.format("{{%s}}", value.getTypeName().getName()));
                    }
                    seenTypeStack.push(value.getTypeName());
                    String unionTypes = value.getUnion().stream()
                            .map(FieldDefinition::getFieldName)
                            .map(FieldName::get)
                            .collect(Collectors.joining("|"));
                    ObjectNode templates = objectMapper.createObjectNode();
                    value.getUnion()
                            .forEach(field -> templates.set(
                                    field.getFieldName().get(), field.getType().accept(visitor)));
                    JsonNode union = objectMapper
                            .createObjectNode()
                            .put("type", unionTypes)
                            .set("oneOf", templates);
                    assert seenTypeStack.pop().equals(value.getTypeName());
                    return union;
                }
            }

            @Override
            public JsonNode visitUnknown(String _unknownType) {
                return new TextNode("{{UNKNOWN}}");
            }
        });
    }

    @Override
    public JsonNode visitExternal(ExternalReference value) {
        return new TextNode(String.format("{{%s}}", value.getExternalReference().getName()));
    }

    @Override
    public JsonNode visitUnknown(String _unknownType) {
        return new TextNode("{{UNKNOWN}}");
    }
}
