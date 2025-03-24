package com.citc.nce.robot.api;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.robot.req.SendPageReq;
import com.citc.nce.robot.vo.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(value = "im-service",contextId="SendDetailsApi", url = "${im:}")
public interface SendDetailsApi {

//    /**
//     * 发送明细查询接口
//     * @param pageReq 请求参数
//     * @return 分页结果
//     */
//    @PostMapping("/im/message/selectAll")
//    PageResult<RobotPhoneResultResp> selectAll(@RequestBody SendPageReq pageReq);

//    /**
//     * 发送明细查询接口
//     * @param pageReq 请求参数
//     * @return 分页结果
//     */
//    @PostMapping("/im/message/media/selectAll")
//    PageResult<RobotPhoneResultResp> selectAllMediaSend(@RequestBody SendPageReq pageReq);

//    /**
//     * 发送明细查询接口
//     * @param pageReq 请求参数
//     * @return 分页结果
//     */
//    @PostMapping("/robot")
//    PageResult<RobotPhoneResultResp> selectAllShortMsgSend(@RequestBody SendPageReq pageReq);

//    /**
//     * 撤回接口
//     * @param idReq 请求id
//     * @return 撤回结果
//     */
//    @PostMapping("/im/message/withdraw")
//    Boolean withdraw(@RequestBody IdReq idReq);

//    /**
//     * 根据时间查询明细
//     * @param idReq planId
//     * @return 撤回结果
//     */
//    @PostMapping("/im/message/querySendDetail")
//    Map<String, List<SendDetailResp>> querySendDetail(@RequestBody IdReq idReq);
//
//
//    @PostMapping("/im/message/querySendDetailByHour")
//    Map<String, List<SendDetailResp>> querySendDetailByHour(@RequestBody SendPageReq pageReq);
//
//    /**
//     * 根绝节点查询所有数据
//     * @param idReq detailId
//     * @return 根据时间线的结果
//     */
//    @PostMapping("/im/message/querySendDetailByDetailId")
//    Map<String, List<SendDetailResp>> querySendDetailByDetailId(@RequestBody IdReq idReq);
//
//    /**
//     * 通过运营商获取所有数据
//     * @param pageReq 时间和运营商名称
//     * @return 结果集
//     */
//    @PostMapping("/im/message/querySendDetailByOperator")
//    List<SendDetailResp> querySendDetailByOperator(@RequestBody SendPageReq pageReq);

//    /**
//     * 通过运营商获取所有数据(按发送来源分类)
//     * @param pageReq 时间和运营商名称
//     * @return 结果集
//     */
//    @PostMapping("/im/message/querySendDetailByResource")
//    List<SendDetailResp> querySendDetailByResource(@RequestBody SendPageReq pageReq);
//
//    @PostMapping("/im/message/querySendDetailGroupByOperator")
//    Map<String, List<SendDetailResp>> querySendDetailGroupByOperator(@RequestBody SendPageReq pageReq);

    /**
     * 首页查询
     * @param pageReq 请求参数
     * @return 返回值
     */
    @PostMapping("/im/message/queryHomePage")
    HomePageResp queryHomePage(@RequestBody SendPageReq pageReq);


    /**
     * 首页查询
     * @param pageReq 请求参数
     * @return 返回值
     */
    @PostMapping("/im/message/queryHomeBrokenLineNum")
    HomePageResp queryHomeBrokenLineNum(@RequestBody SendPageReq pageReq);


    /**
     * 首页查询（昨天数据）
     * @return 返回值
     */
    @PostMapping("/im/message/queryHomeBrokenLineNumByHour")
    HomePageResp queryHomeBrokenLineNumByHour();
}
