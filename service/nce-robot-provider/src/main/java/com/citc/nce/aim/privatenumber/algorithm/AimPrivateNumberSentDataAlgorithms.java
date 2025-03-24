package com.citc.nce.aim.privatenumber.algorithm;

import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AimPrivateNumberSentDataAlgorithms implements StandardShardingAlgorithm<Date> {

    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Date> shardingValue) {
        String logicTableName = shardingValue.getLogicTableName();
        LocalDateTime dateTime = shardingValue.getValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return logicTableName + "_" + dateTime.format(formatter);
    }


    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, RangeShardingValue<Date> shardingValue) {
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
