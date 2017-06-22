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

import com.hazelcast.jet.pipeline.PEnd;
import com.hazelcast.jet.pipeline.Pipeline;

/**
 * Javadoc pending.
 */
public class PEndImpl implements PEnd {

    private final Pipeline pipeline;

    public PEndImpl(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    @Override
    public Pipeline getPipeline() {
        return pipeline;
    }
}
