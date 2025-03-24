package com.citc.nce.readingLetter.algorithm;

import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;

import java.util.Collection;
import java.util.Properties;

public class ReadingLetterParseRecordShardingAlgorithms implements StandardShardingAlgorithm<Long> {
    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Long> shardingValue) {
        String logicTableName = shardingValue.getLogicTableName();
        String customerId = String.valueOf(shardingValue.getValue());
        if (customerId.length() == 10) {
            return logicTableName + "_" + customerId;
        }
        if (customerId.length() != 15) {
            return null;
        }
        String cspId = customerId.substring(0, 10);
        if (!availableTargetNames.contains(logicTableName + "_" + cspId)) {
            return null;
        }
        return logicTableName + "_" + cspId;
    }

    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, RangeShardingValue<Long> shardingValue) {
        return availableTargetNames;
    }

    @Override
    public Properties getProps() {
        return null;
    }

    @Override
    public void init(Properties props) {

    }
}
