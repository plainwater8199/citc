package com.citc.nce.im.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.im.entity.RobotMassQuantityStatisticsDo;
import com.citc.nce.robot.req.SendPageReq;
import com.citc.nce.robot.vo.HomePageResp;

public interface IRobotMassQuantityStatisticsService {

    /**
     * 查询首页数据
     * @param pageReq 请求参数
     * @return 首页数据
     */
    HomePageResp queryHomePage(SendPageReq pageReq);

    /**
     * 首页折线图查询
     * @param pageReq 请求参数
     * @return 查询结果
     */
    HomePageResp queryHomeBrokenLineNum(SendPageReq pageReq);

    /**
     * 首页折线图查询(昨天)
     * @return 查询结果
     */
    HomePageResp queryHomeBrokenLineNumByHour();
}
