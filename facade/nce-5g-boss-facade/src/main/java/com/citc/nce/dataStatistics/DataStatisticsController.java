package com.citc.nce.dataStatistics;

import com.citc.nce.annotation.BossAuth;
import com.citc.nce.authcenter.auth.AdminAuthApi;
import com.citc.nce.authcenter.auth.vo.CspCustomerChatbotAccountVo;
import com.citc.nce.authcenter.auth.vo.req.QuerySupplierChatbotReq;
import com.citc.nce.authcenter.auth.vo.resp.AdminUserSumResp;
import com.citc.nce.authcenter.auth.vo.resp.ChatbotProcessingSumResp;
import com.citc.nce.authcenter.auth.vo.resp.CspCustomerChatbotAccountListResp;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.dataStatistics.vo.req.OperatorTypePageReq;
import com.citc.nce.dataStatistics.vo.req.OperatorTypeReq;
import com.citc.nce.dataStatistics.vo.resp.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: ylzouf
 * @Date: 2022/10/27 17:24
 * @Version 1.0
 * @Description:
 */
@BossAuth("/chatbot-view/statistic-analysis/robot")
@RestController
@Api(tags = "数据统计模块")
public class DataStatisticsController {

    @Resource
    private DataStatisticsApi dataStatisticsApi;

    @Resource
    private AdminAuthApi adminAuthApi;

    @ApiOperation("管理平台---机器人服务分析")
    @GetMapping("/data/chatbotServiceAnalysis")
    public RobotServiceAnalysisResp chatbotServiceAnalysis() {
        return dataStatisticsApi.chatbotServiceAnalysis();
    }

    @ApiOperation("管理平台---昨日交互会话折线图")
    @PostMapping("/data/converInteractYesterdayLineChart")
    public ConversationalInteractionResp converInteractYesterdayLineChart(@RequestBody OperatorTypeReq req) {
        return dataStatisticsApi.converInteractYesterdayLineChart(req);
    }



    @ApiOperation("管理平台---昨日交互会话分页")
    @PostMapping("/data/converInteractYesterdayPage")
    public PageResult converInteractYesterdayPage(@RequestBody OperatorTypePageReq req) {
        return dataStatisticsApi.converInteractYesterdayPage(req);
    }

    @BossAuth("/chatbot-view/index")
    @ApiOperation("管理平台---首页上行趋势")
    @PostMapping("/data/converInteractHeadUpChart")
    public List<ConversationalHeadUpChartResp> converInteractHeadUpChart(@RequestBody OperatorTypeReq req) {
        return dataStatisticsApi.converInteractHeadUpChart(req);
    }

    /**
     * 平台使用申请待审核数量
     *
     * @param
     * @return
     */
    @BossAuth("/chatbot-view/index")
    @ApiOperation("管理平台---待审核申请")
    @GetMapping("/admin/user/getPlatformApplicationReviewSum")
    public AdminUserSumResp getPlatformApplicationReviewSum() {
        return adminAuthApi.getPlatformApplicationReviewSum();
    }

    /**
     * 待处理Chatbot申请
     *
     */
    @BossAuth("/chatbot-view/index")
    @ApiOperation("管理平台---待审核申请")
    @GetMapping("/admin/user/getSupplierChatbotProcessingSum")
    public ChatbotProcessingSumResp getSupplierChatbotProcessingSum() {
        return adminAuthApi.getSupplierChatbotProcessingSum();
    }


    /**
     * csp 供应商渠道Chatbot申请待审核列表
     *
     * @param
     * @return
     */
    @ApiOperation("管理平台---CSP的Chatbot(supplier)列表")
    @GetMapping("/admin/user/getSupplierChatbot")
    public PageResult<CspCustomerChatbotAccountVo>  getSupplierChatbot(QuerySupplierChatbotReq req) {
        return adminAuthApi.getSupplierChatbot(req);
    }
}


