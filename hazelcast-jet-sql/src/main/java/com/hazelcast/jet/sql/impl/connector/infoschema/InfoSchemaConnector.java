/*
 * Copyright (c) 2008-2020, Hazelcast, Inc. All Rights Reserved.
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

package com.hazelcast.jet.sql.impl.connector.infoschema;

import com.hazelcast.jet.Traverser;
import com.hazelcast.jet.Traversers;
import com.hazelcast.jet.core.AbstractProcessor;
import com.hazelcast.jet.core.DAG;
import com.hazelcast.jet.core.ProcessorSupplier;
import com.hazelcast.jet.core.Vertex;
import com.hazelcast.jet.sql.impl.connector.SqlConnector;
import com.hazelcast.jet.sql.impl.ExpressionUtil;
import com.hazelcast.jet.sql.impl.schema.MappingField;
import com.hazelcast.spi.impl.NodeEngine;
import com.hazelcast.sql.impl.expression.Expression;
import com.hazelcast.sql.impl.schema.Table;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

import static com.hazelcast.jet.core.ProcessorMetaSupplier.forceTotalParallelismOne;

/**
 * A connector for tables in the {@code information_schema}.
 */
final class InfoSchemaConnector implements SqlConnector {

    public static final InfoSchemaConnector INSTANCE = new InfoSchemaConnector();

    private static final String TYPE_NAME = "InformationSchema";

    private InfoSchemaConnector() {
    }

    @Override
    public String typeName() {
        return TYPE_NAME;
    }

    @Override
    public boolean isStream() {
        return false;
    }

    @Nonnull @Override
    public List<MappingField> resolveAndValidateFields(
            @Nonnull NodeEngine nodeEngine,
            @Nonnull Map<String, String> options,
            @Nonnull List<MappingField> userFields
    ) {
        throw new UnsupportedOperationException();
    }

    @Nonnull @Override
    public Table createTable(
            @Nonnull NodeEngine nodeEngine,
            @Nonnull String schemaName,
            @Nonnull String mappingName,
            @Nonnull String externalName,
            @Nonnull Map<String, String> options,
            @Nonnull List<MappingField> resolvedFields
    ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean supportsFullScanReader() {
        return true;
    }

    @Nonnull @Override
    public Vertex fullScanReader(
            @Nonnull DAG dag,
            @Nonnull Table table0,
            @Nullable Expression<Boolean> predicate,
            @Nonnull List<Expression<?>> projection
    ) {
        InfoSchemaTable table = (InfoSchemaTable) table0;

        List<Object[]> rows = ExpressionUtil.evaluate(predicate, projection, table.rows());

        return dag.newUniqueVertex(
                table.toString(),
                forceTotalParallelismOne(ProcessorSupplier.of(() -> new StaticSourceP(rows)))
        );
    }

    private static final class StaticSourceP extends AbstractProcessor {

        private final List<Object[]> rows;

        private Traverser<Object[]> traverser;

        private StaticSourceP(List<Object[]> rows) {
            this.rows = rows;
        }

        @Override
        protected void init(@Nonnull Context context) {
            traverser = Traversers.traverseIterable(rows);
        }

        @Override
        public boolean complete() {
            return emitFromTraverser(traverser);
        }
    }
}
