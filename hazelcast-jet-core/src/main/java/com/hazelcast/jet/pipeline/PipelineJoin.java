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

import java.util.Map.Entry;

import static com.hazelcast.jet.pipeline.JoinOn.onKeys;

public class PipelineJoin {

    public static void main(String[] args) {

        Pipeline p = Pipeline.create();
        PStream<Trade> trades = p.drawFrom(Sources.<Integer, Trade>readMap("trades"))
                                 .map(Entry::getValue);

        PStream<Product> products = p.drawFrom(Sources.<Integer, Product>readMap("products"))
                                     .map(Entry::getValue);

        PStream<Broker> brokers = p.drawFrom(Sources.<Integer, Broker>readMap("brokers"))
                                   .map(Entry::getValue);

        PStream<Tuple3<Trade, Product, Broker>> joined = trades.join(
                products, onKeys(Trade::getProduct, Product::getId),
                brokers, onKeys(Trade::getBroker, Broker::getId));
    }

    private static class Trade {

        public Integer getProduct() {
            return null;
        }

        public Integer getBroker() {
            return null;
        }
    }

    private static class Product {

        public Integer getId() {
            return null;
        }
    }

    private static class Broker {
        public Integer getId() {
            return null;
        }

    }
}
