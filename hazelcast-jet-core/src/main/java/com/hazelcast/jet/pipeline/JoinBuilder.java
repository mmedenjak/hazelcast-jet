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

import com.hazelcast.jet.pipeline.impl.transform.HashJoinTransform;
import com.hazelcast.jet.pipeline.impl.PipelineImpl;
import com.hazelcast.jet.pipeline.bag.BagsByTag;
import com.hazelcast.jet.pipeline.bag.Tag;
import com.hazelcast.jet.pipeline.tuple.Tuple2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

/**
 * Javadoc pending.
 */
public class JoinBuilder<E_LEFT> {
    private final Map<Tag<?>, JoinClause<?, E_LEFT, ?>> clauses = new HashMap<>();

    // Holds the TupleIndex of the "left-hand" component of the join operation.
    // This JoinClause instance is a special case which has no JoinOn.
    // The pstream it holds is the implied left-hand side of all join clauses.
    private final Tag<E_LEFT> leftIndex;

    JoinBuilder(PStream<E_LEFT> leftStream) {
        this.leftIndex = add(leftStream, null);
    }

    public Tag<E_LEFT> leftTag() {
        return leftIndex;
    }

    public <K, E_RIGHT> Tag<E_RIGHT> add(PStream<E_RIGHT> s, JoinOn<K, E_LEFT, E_RIGHT> joinOn) {
        Tag<E_RIGHT> ind = new Tag<>(clauses.size());
        clauses.put(ind, new JoinClause<>(s, joinOn));
        return ind;
    }

    public PStream<Tuple2<E_LEFT, BagsByTag>> build() {
        List<PStream> upstream = orderedClauses()
                .map(e -> e.getValue().pstream())
                .collect(toList());
        HashJoinTransform transform = new HashJoinTransform(
                orderedClauses()
                        .skip(1)
                        .map(e -> e.getValue().joinOn())
                        .collect(toList()),
                BagsByTag.class);

        PipelineImpl pipeline = (PipelineImpl) clauses.get(leftIndex).pstream().getPipeline();
        return pipeline.attach(upstream, transform);
    }

    private Stream<Entry<Tag<?>, JoinClause<?, E_LEFT, ?>>> orderedClauses() {
        return clauses.entrySet().stream()
                      .sorted(comparing(Entry::getKey));
    }

    private static class JoinClause<K, E_LEFT, E_RIGHT> {
        private final PStream<E_RIGHT> pstream;
        private final JoinOn<K, E_LEFT, E_RIGHT> joinOn;

        JoinClause(PStream<E_RIGHT> pstream, JoinOn<K, E_LEFT, E_RIGHT> joinOn) {
            this.pstream = pstream;
            this.joinOn = joinOn;
        }

        PStream<E_RIGHT> pstream() {
            return pstream;
        }

        JoinOn<K, E_LEFT, E_RIGHT> joinOn() {
            return joinOn;
        }
    }
}
