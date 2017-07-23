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

package com.hazelcast.jet.pipeline.bag;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Javadoc pending.
 */
public class BagsByTag {
    private final Map<Tag<?>, Collection> components = new HashMap<>();

    public <E> Collection<E> bag(Tag<E> k) {
        return (Collection<E>) components.get(k);
    }

    public <E> void put(Tag<E> t, Collection<E> bag) {
        components.put(t, bag);
    }

    public void combineWith(BagsByTag bagsByTag) {

    }
}
