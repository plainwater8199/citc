package com.citc.nce.tenant.service.impl;

import com.citc.nce.seata.ShardingJdbcNodesAutoConfigurator;
import com.citc.nce.tenant.robot.dao.RobotMassQuantityStatistics1Dao;
import com.citc.nce.tenant.robot.entity.RobotMassQuantityStatisticsDo;
import com.citc.nce.tenant.service.RefreshActualTableService;
import com.citc.nce.tenant.vo.req.CreateTableReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RefreshActualTableServiceImpl implements RefreshActualTableService {

    @Autowired
    private ShardingJdbcNodesAutoConfigurator shardingJdbcNodesAutoConfigurator;
    @Autowired
    private RobotMassQuantityStatistics1Dao robotMassQuantityStatistics1Dao;
    @Override
    public void refreshActualTable(CreateTableReq req) {


        List<RobotMassQuantityStatisticsDo> robotMassQuantityStatisticsDos = robotMassQuantityStatistics1Dao.selectList();
        System.out.println("------------刷新前："+robotMassQuantityStatisticsDos.size());

        List<String> nodesName = new ArrayList<>();
        nodesName.add("robot.robot_mass_quantity_statistics_7514153566");
        nodesName.add("robot.robot_mass_quantity_statistics_6307167392");
        shardingJdbcNodesAutoConfigurator.generateActualDataNodes("robot_mass_quantity_statistics",nodesName);
        List<RobotMassQuantityStatisticsDo> robotMassQuantityStatisticsDos2 = robotMassQuantityStatistics1Dao.selectList();
        System.out.println("------------刷新后："+robotMassQuantityStatisticsDos2.size());


        List<String> nodesName2 = new ArrayList<>();
        nodesName2.add("robot.robot_mass_quantity_statistics_7514153566");
        shardingJdbcNodesAutoConfigurator.generateActualDataNodes("robot_mass_quantity_statistics",nodesName2);
        List<RobotMassQuantityStatisticsDo> robotMassQuantityStatisticsDos3 = robotMassQuantityStatistics1Dao.selectList();
        System.out.println("------------刷新3后："+robotMassQuantityStatisticsDos3.size());

    }
}
