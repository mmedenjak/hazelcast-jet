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

package com.hazelcast.jet.pipeline.impl;

import com.hazelcast.jet.DAG;
import com.hazelcast.jet.pipeline.PEnd;
import com.hazelcast.jet.pipeline.PStream;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sink;
import com.hazelcast.jet.pipeline.Source;
import com.hazelcast.jet.pipeline.impl.transform.JoinTransform;
import com.hazelcast.jet.pipeline.impl.transform.UnaryTransform;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;

public class PipelineImpl implements Pipeline {

    final Map<AbstractPElement, List<AbstractPElement>> adjacencyMap = new HashMap<>();

    @Override
    public <E> PStream<E> drawFrom(Source<E> source) {
        return new PStreamImpl<>(emptyList(), source, this);
    }

    @Nonnull @Override
    public DAG toDag() {
        return new Planner(this).createDag();
    }

    public <IN, OUT> PStream<OUT> transform(PStreamImpl<IN> input, UnaryTransform<? super IN, OUT> unaryTransform) {
        PStreamImpl<OUT> output = new PStreamImpl<>(input, unaryTransform, this);
        addEdge(input, output);
        return output;
    }

    public PStream join(List<PStream> upstream, JoinTransform joinTransform) {
        PStreamImpl attached = new PStreamImpl(upstream, joinTransform, this);
        upstream.forEach(u -> addEdge((PStreamImpl) u, attached));
        return attached;
    }

    public <E> PEnd drainTo(PStreamImpl<E> input, Sink sink) {
        PEndImpl output = new PEndImpl(input, sink, this);
        addEdge(input, output);
        return output;
    }

    private void addEdge(AbstractPElement source, AbstractPElement dest) {
        adjacencyMap.computeIfAbsent(source, e -> new ArrayList<>()).add(dest);
    }
}