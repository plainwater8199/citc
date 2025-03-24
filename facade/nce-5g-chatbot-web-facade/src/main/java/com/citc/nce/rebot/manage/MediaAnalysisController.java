//package com.citc.nce.rebot.manage;
//
//import cn.hutool.core.collection.CollectionUtil;
//import cn.hutool.core.util.ObjectUtil;
//import com.citc.nce.robot.api.MassAnalysisApi;
//import com.citc.nce.robot.api.MediaAnalysisApi;
//import com.citc.nce.robot.req.SendPageReq;
//import com.citc.nce.robot.vo.*;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//
//import org.springframework.beans.BeanUtils;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.annotation.Resource;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@RestController
//@Slf4j
//public class MediaAnalysisController implements MediaAnalysisApi {
//
//    @Resource
//    MediaAnalysisApi massAnalysisApi;
//
//
//    @PostMapping({"/im/message/media/queryAnalysisByPlan"})
//    @Override
//    public AnalysisPage queryAnalysisByPlan(@RequestBody SendPageReq sendPageReq) {
//        return massAnalysisApi.queryAnalysisByPlan(sendPageReq);
//    }
//
//    @PostMapping({"/im/message/media/queryManageHome"})
//    @Override
//    public AnalysisResp queryManageHome(@RequestBody SendPageReq sendPageReq) {
//        return massAnalysisApi.queryManageHome(sendPageReq);
//    }
//
//    @PostMapping({"/im/message/media/queryManageGroupByOperator"})
//    @Override
//    public List<OperatorResp> queryManageGroupByOperator(@RequestBody SendPageReq sendPageReq) {
//        return massAnalysisApi.queryManageGroupByOperator(sendPageReq);
//    }
//
//    @PostMapping({"/im/message/media/querySendAmountByOperator"})
//    @Override
//    public List<AnalysisResp> querySendAmountByOperator(@RequestBody SendPageReq sendPageReq) {
//        return massAnalysisApi.querySendAmountByOperator(sendPageReq);
//    }
//
//    @PostMapping({"/im/message/media/querySendAmountByDetailId"})
//    @Override
//    public AnalysisPage querySendAmountByDetailId(@RequestBody SendPageReq sendPageReq) {
//        return massAnalysisApi.querySendAmountByDetailId(sendPageReq);
//    }
//
//    @PostMapping({"/im/message/media/queryPieChartByDetailId"})
//    @Override
//    public List<AnalysisResp> queryPieChartByDetailId(@RequestBody SendPageReq sendPageReq) {
//        return massAnalysisApi.queryPieChartByDetailId(sendPageReq);
//    }
//
//    @PostMapping({"/im/message/media/queryCspSendAmount"})
//    @Override
//    public OperatorResp queryCspSendAmount() {
//        return massAnalysisApi.queryCspSendAmount();
//    }
//
//    @PostMapping({"/im/message/media/queryActiveChatBot"})
//    @Override
//    public List<OperatorResp> queryActiveChatBot(@RequestBody SendPageReq sendPageReq) {
//        return massAnalysisApi.queryActiveChatBot(sendPageReq);
//    }
//
//    @PostMapping({"/im/message/media/massAnalysisTest"})
//    @Override
//    public void massAnalysisTest(SendPageReq sendPageReq) {
//
//    }
//
//    @PostMapping({"/im/message/media/queryCspOperatorSendAmount"})
//    @Override
//    public OperatorResp queryCspOperatorSendAmount() {
//        return massAnalysisApi.queryCspOperatorSendAmount();
//    }
//
//
//    @PostMapping({"/im/message/media/queryCspSendAmountLine"})
//    @Override
//    public List<OperatorResp> queryCspSendAmountLine(@RequestBody SendPageReq sendPageReq) {
//        return massAnalysisApi.queryCspSendAmountLine(sendPageReq);
//    }
//
//}
