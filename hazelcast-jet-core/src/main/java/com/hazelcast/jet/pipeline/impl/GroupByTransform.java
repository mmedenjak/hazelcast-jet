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

import com.hazelcast.jet.AggregateOperation;
import com.hazelcast.jet.function.DistributedFunction;
import com.hazelcast.jet.pipeline.UnaryTransform;

import java.util.Map.Entry;

/**
 * Javadoc pending.
 */
public class GroupByTransform<E, K, R> implements UnaryTransform<E, Entry<K, R>> {
    private final DistributedFunction<? super E, ? extends K> keyF;
    private final AggregateOperation<E, ?, R> aggrOp;

    public GroupByTransform(DistributedFunction<? super E, ? extends K> keyF, AggregateOperation<E, ?, R> aggrOp) {
        this.keyF = keyF;
        this.aggrOp = aggrOp;
    }
}
