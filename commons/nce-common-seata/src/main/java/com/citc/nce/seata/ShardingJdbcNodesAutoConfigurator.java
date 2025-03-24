package com.citc.nce.seata;

import cn.hutool.extra.spring.SpringUtil;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.driver.jdbc.core.datasource.ShardingSphereDataSource;
import org.apache.shardingsphere.infra.datanode.DataNode;
import org.apache.shardingsphere.infra.rule.ShardingSphereRule;
import org.apache.shardingsphere.infra.util.expr.InlineExpressionParser;
import org.apache.shardingsphere.mode.manager.ContextManager;
import org.apache.shardingsphere.sharding.rule.ShardingRule;
import org.apache.shardingsphere.sharding.rule.TableRule;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.Advised;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author bydud
 * @since 2024/3/5
 */

@Slf4j
public class ShardingJdbcNodesAutoConfigurator {

    private final static String schemaName = "logic_db";

    public ContextManager getContextManager() {
        try {
            ShardingSphereDataSource bean;
//            boolean seataEnable = Boolean.parseBoolean(SpringUtil.getProperty("seata.enabled"));
//            if (seataEnable) {
//                Map<String, Advised> beansOfType = SpringUtil.getBeansOfType(Advised.class);
//                Advised shardingSphereDataSource = beansOfType.get("shardingSphereDataSource");
//                TargetSource targetSource = shardingSphereDataSource.getTargetSource();
//                bean = (ShardingSphereDataSource) targetSource.getTarget();
//            } else {
                bean = (ShardingSphereDataSource) SpringUtil.getBean("shardingSphereDataSource", DataSource.class);
//            }
            if (Objects.isNull(bean)) {
                bean = SpringUtil.getBean(ShardingSphereDataSource.class);
            }
            Field field = ShardingSphereDataSource.class.getDeclaredField("contextManager");
            field.setAccessible(true);
            return (ContextManager) field.get(bean);
        } catch (Exception e) {
            log.error("获取shardingSphereDataSource.contextManager失败", e);
            throw new RuntimeException();
        }
    }


    public void generateActualDataNodes(String logicTableName, List<String> actualDataNodes) {
        // generate actualDataNodes
        this.updateShardRuleActualDataNodes(schemaName, logicTableName, String.join(",", actualDataNodes));
    }

    public void updateShardRuleActualDataNodes(String schemaName, String logicTableName, String actualDataNodes) {
        // 根据inline 表达式转换DataNode节点
        List<String> newStrDataNodes = new InlineExpressionParser(actualDataNodes).splitAndEvaluate();
        //sharding数据源
        ContextManager contextManager = getContextManager();

        //所有的拆分表
        Collection<ShardingSphereRule> tableRules = contextManager.getMetaDataContexts()
                .getMetaData()
                .getDatabase(schemaName)
                .getRuleMetaData()
                .getRules();
        try {
            for (ShardingSphereRule shardingSphereRule : tableRules) {
                if (shardingSphereRule instanceof ShardingRule) {
                    ShardingRule rule = (ShardingRule) shardingSphereRule;
                    TableRule tableRule = rule.getTableRule(logicTableName);
                    // 动态刷新actualDataNodesField
                    Field actualDataNodesField = TableRule.class.getDeclaredField("actualDataNodes");
                    Field modifiersField = Field.class.getDeclaredField("modifiers");
                    modifiersField.setAccessible(true);
                    // 设置修饰符：private
                    modifiersField.setInt(actualDataNodesField, actualDataNodesField.getModifiers() & ~Modifier.FINAL);
                    //  新节点 循环动态拼接所有节点表名
                    List<DataNode> newDataNodes = newStrDataNodes.stream().map(DataNode::new).collect(Collectors.toList());
                    actualDataNodesField.setAccessible(true);
                    // 数据更新回去
                    actualDataNodesField.set(tableRule, newDataNodes);
                    Set<String> actualTables = Sets.newHashSet();
                    // dataNodeIntegerMap
                    Map<DataNode, Integer> dataNodeIntegerMap = Maps.newHashMap();
                    newDataNodes.forEach(LambadaTools.forEachWithIndex(dataNodeIntegerMap::put));

                    Map<String, List<DataNode>> dataSourceNodes = newDataNodes.stream().collect(Collectors.groupingBy(DataNode::getDataSourceName));

                    // 动态刷新：actualTables
                    Field actualTablesField = TableRule.class.getDeclaredField("actualTables");
                    actualTablesField.setAccessible(true);
                    actualTablesField.set(tableRule, actualTables);

                    // 动态刷新：dataNodeIndexMap
                    Field dataNodeIndexMapField = TableRule.class.getDeclaredField("dataNodeIndexMap");
                    dataNodeIndexMapField.setAccessible(true);
                    dataNodeIndexMapField.set(tableRule, dataNodeIntegerMap);

                    // 动态刷新：datasourceToTablesMap
                    Map<String, Collection<String>> datasourceToTablesMap = Maps.newHashMap();
                    // 不同数据源，表不一样
                    dataSourceNodes.forEach((ds, node) -> {
                        datasourceToTablesMap.put(ds, node.stream().map(DataNode::getTableName).collect(Collectors.toSet()));
                    });

                    Field datasourceToTablesMapField = TableRule.class.getDeclaredField("dataSourceToTablesMap");
                    datasourceToTablesMapField.setAccessible(true);
                    datasourceToTablesMapField.set(tableRule, datasourceToTablesMap);
                }
            }
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            log.error("刷新节点报错了", e);
        }
    }
}