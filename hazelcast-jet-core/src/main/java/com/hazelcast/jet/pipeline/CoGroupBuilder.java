/*
 * Copyright (c) 2008-2017, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.jet.pipeline;

import com.hazelcast.jet.function.DistributedFunction;
import com.hazelcast.jet.pipeline.bag.BagsByTag;
import com.hazelcast.jet.pipeline.impl.PStreamImpl;
import com.hazelcast.jet.pipeline.impl.PipelineImpl;
import com.hazelcast.jet.pipeline.tuple.Tuple2;
import com.hazelcast.jet.pipeline.bag.Tag;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

/**
 * Javadoc pending.
 */
public class CoGroupBuilder<K, E_LEFT> {
    private final Map<Tag<?>, CoGroupClause<?, K>> clauses = new HashMap<>();

    private final Tag<E_LEFT> leftTag;

    public CoGroupBuilder(PStream<E_LEFT> s, DistributedFunction<? super E_LEFT, K> groupKeyF) {
        this.leftTag = add(s, groupKeyF);
    }

    public Tag<E_LEFT> leftTag() {
        return leftTag;
    }

    public <E> Tag<E> add(PStream<E> s, DistributedFunction<? super E, K> groupKeyF) {
        Tag tag = new Tag(clauses.size());
        clauses.put(tag, new CoGroupClause<>(s, groupKeyF));
        return (Tag<E>) tag;
    }

    public <R> PStream<Tuple2<K, R>> build(GroupAggregation<BagsByTag, ?, R> cogOp) {
        return new PStreamImpl<>(
                orderedClauses()
                        .skip(1)
                        .map(e -> e.getValue().pstream())
                        .collect(toList()),
                new CoGroupTransform<>(orderedClauses()
                        .map(e -> e.getValue().groupKeyF())
                        .collect(toList()),
                        cogOp,
                        BagsByTag.class
                ),
                (PipelineImpl) clauses.get(leftTag).pstream.getPipeline()
        );
    }

    private Stream<Entry<Tag<?>, CoGroupClause<?, K>>> orderedClauses() {
        return clauses.entrySet().stream()
                      .sorted(comparing(Entry::getKey));
    }

    private static class CoGroupClause<E, K> {
        private final PStream<E> pstream;
        private final DistributedFunction<? super E, K> groupKeyF;

        CoGroupClause(PStream<E> pstream, DistributedFunction<? super E, K> groupKeyF) {
            this.pstream = pstream;
            this.groupKeyF = groupKeyF;
        }

        PStream<E> pstream() {
            return pstream;
        }

        DistributedFunction<? super E, K> groupKeyF() {
            return groupKeyF;
        }
    }
}
