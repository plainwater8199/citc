package com.citc.nce.csp;

import com.citc.nce.auth.csp.statistics.CspStatisticsApi;
import com.citc.nce.auth.csp.statistics.vo.CspStatisticsIndustryTypeResp;
import com.citc.nce.auth.csp.statistics.vo.GetChatbotIndustryStatisticForCMCCResp;
import com.citc.nce.auth.csp.statistics.vo.req.QueryUserStatisticByOperatorReq;
import com.citc.nce.auth.csp.statistics.vo.resp.GetCustomerIndustryStatisticForCMCCResp;
import com.citc.nce.auth.csp.statistics.vo.resp.QueryUserStatisticByOperatorResp;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.authcenter.csp.vo.CustomerProvinceResp;
import com.citc.nce.robot.RobotRecordApi;
import com.citc.nce.robot.vo.SendQuantityResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:35
 */
@RestController
@RequestMapping("/csp")
@Api(value = "StatisticsController",tags = "CSP--数据统计")
public class StatisticsController {

    @Autowired
    private CspStatisticsApi cspStatisticsApi;
    @Resource
    private RobotRecordApi robotRecordApi;

    @Autowired
    private CspCustomerApi cspCustomerApi;

    @GetMapping("/statistics/queryUserProvince")
    @ApiOperation(value = "查询客户地区分布", notes = "查询客户地区分布")
    public List<CustomerProvinceResp> queryUserProvince(){
        return cspCustomerApi.getCustomerDistribution();
    }


    @GetMapping("/statistics/getChatbotIndustryStatisticForCMCC")
    @ApiOperation(value = "统一运营管理平台-移动chatbot绑定行业分布", notes = "统一运营管理平台-移动chatbot绑定行业分布")
    public GetChatbotIndustryStatisticForCMCCResp getChatbotIndustryStatisticForCMCC() {
        return cspStatisticsApi.getChatbotIndustryStatisticForCMCC();
    }

    @GetMapping("/statistics/getCustomerIndustryStatisticForCMCC")
    @ApiOperation(value = "统一运营管理平台-移动5G消息用户行业分布", notes = "统一运营管理平台-移动5G消息用户行业分布")
    public GetCustomerIndustryStatisticForCMCCResp getCustomerIndustryStatisticForCMCC() {
        return cspStatisticsApi.getCustomerIndustryStatisticForCMCC();
    }


    @ApiOperation(value = "查询通道发送量占比", notes = "查询通道发送量占比")
    @GetMapping("/statistics/queryChannelSendQuantity")
    public List<SendQuantityResp> queryChannelSendQuantity(){
        return robotRecordApi.queryChannelSendQuantity();
    }


    @ApiOperation(value = "5G消息应用开发平台-数据统计-会话分析--新增用户和活跃用户分布")
    @PostMapping("/statistics/queryUserStatisticByOperator")
    public QueryUserStatisticByOperatorResp queryUserStatisticByOperator(@RequestBody QueryUserStatisticByOperatorReq req){
        return cspStatisticsApi.queryUserStatisticByOperator(req);
    }
}
