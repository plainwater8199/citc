/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.transaction.base.seata.at;

import com.google.common.base.Preconditions;
import io.seata.config.FileConfiguration;
import io.seata.core.context.RootContext;
import io.seata.core.exception.TransactionException;
import io.seata.core.rpc.netty.RmNettyRemotingClient;
import io.seata.core.rpc.netty.TmNettyRemotingClient;
import io.seata.rm.RMClient;
import io.seata.rm.datasource.DataSourceProxy;
import io.seata.tm.TMClient;
import io.seata.tm.api.GlobalTransaction;
import io.seata.tm.api.GlobalTransactionContext;
import io.seata.tm.api.GlobalTransactionRole;
import lombok.SneakyThrows;
import org.apache.shardingsphere.infra.database.type.DatabaseType;
import org.apache.shardingsphere.transaction.core.ResourceDataSource;
import org.apache.shardingsphere.transaction.core.TransactionType;
import org.apache.shardingsphere.transaction.spi.ShardingSphereTransactionManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Seata AT transaction manager.
 */
public final class SeataATShardingSphereTransactionManager implements ShardingSphereTransactionManager {

    private final Map<String, DataSource> dataSourceMap = new HashMap<>();

    private final String applicationId;

    private final String transactionServiceGroup;

    private final boolean enableSeataAT;

    private final int globalTXTimeout;

    public SeataATShardingSphereTransactionManager() {
        FileConfiguration config = new FileConfiguration("seata.conf");
        enableSeataAT = config.getBoolean("sharding.transaction.seata.at.enable", true);
        applicationId = config.getConfig("client.application.id");
        transactionServiceGroup = config.getConfig("client.transaction.service.group", "default");
        globalTXTimeout = config.getInt("sharding.transaction.seata.tx.timeout", 60);
    }

    @Override
    public void init(final DatabaseType databaseType, final Collection<ResourceDataSource> resourceDataSources, final String providerType) {
        if (enableSeataAT) {
            initSeataRPCClient();
            resourceDataSources.forEach(each -> dataSourceMap.put(each.getOriginalName(), new DataSourceProxy(each.getDataSource())));
        }
    }

    private void initSeataRPCClient() {
        Preconditions.checkNotNull(applicationId, "please config application id within seata.conf file.");
        TMClient.init(applicationId, transactionServiceGroup);
        RMClient.init(applicationId, transactionServiceGroup);
    }

    @Override
    public TransactionType getTransactionType() {
        return TransactionType.BASE;
    }

    @Override
    public boolean isInTransaction() {
        Preconditions.checkState(enableSeataAT, "sharding seata-at transaction has been disabled.");
        return null != SeataTransactionHolder.get();
    }

    @Override
    public Connection getConnection(final String databaseName, final String dataSourceName) throws SQLException {
        Preconditions.checkState(enableSeataAT, "sharding seata-at transaction has been disabled.");
        return dataSourceMap.get(databaseName + "." + dataSourceName).getConnection();
    }

    @Override
    public void begin() {
        begin(globalTXTimeout);
    }

    @Override
    @SneakyThrows(TransactionException.class)
    public void begin(final int timeout) {
        if (timeout < 0) {
            throw new TransactionException("timeout should more than 0s");
        }
        Preconditions.checkState(enableSeataAT, "sharding seata-at transaction has been disabled.");
        GlobalTransaction globalTransaction = GlobalTransactionContext.getCurrentOrCreate();
        globalTransaction.begin(timeout * 1000);
        SeataTransactionHolder.set(globalTransaction);
    }

    @Override
    @SneakyThrows(TransactionException.class)
    public void commit(final boolean rollbackOnly) {
        Preconditions.checkState(enableSeataAT, "sharding seata-at transaction has been disabled.");
        GlobalTransaction globalTransaction = SeataTransactionHolder.get();
        try {
            globalTransaction.commit();
        } finally {
            // 当前事务如果只是全局事务参与者（而不是发起者），那么不需要结束全局事务
            GlobalTransactionRole role = Optional.ofNullable(globalTransaction)
                    .map(GlobalTransaction::getGlobalTransactionRole)
                    .orElse(null);
            boolean isParticipant = role == GlobalTransactionRole.Participant;
            if (!isParticipant) {
                SeataTransactionHolder.clear();
                RootContext.unbind();
            }
        }
    }

    @Override
    @SneakyThrows(TransactionException.class)
    public void rollback() {
        Preconditions.checkState(enableSeataAT, "sharding seata-at transaction has been disabled.");
        try {
            SeataTransactionHolder.get().rollback();
        } finally {
            SeataTransactionHolder.clear();
            RootContext.unbind();
        }
    }

    @Override
    public void close() {
        dataSourceMap.clear();
        SeataTransactionHolder.clear();
        RmNettyRemotingClient.getInstance().destroy();
        TmNettyRemotingClient.getInstance().destroy();
    }
}
