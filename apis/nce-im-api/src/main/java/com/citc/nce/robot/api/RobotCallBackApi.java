package com.citc.nce.robot.api;

import com.citc.nce.robot.vo.MaterialSubmitReq;
import com.citc.nce.robot.vo.MaterialSubmitResp;
import com.citc.nce.robot.vo.RobotCallBackParam;
import com.citc.nce.robot.vo.RobotCustomCommandReq;
import com.citc.nce.robot.vo.RobotCustomCommandResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * <p>回调函数</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2022/12/1 11:05
 */
@FeignClient(value = "im-service", contextId = "robotCallBack")
public interface RobotCallBackApi {
    /**
     * 机器人自定义指令回调
     * @param callBackParam
     * @return none
     * @author zy.qiu
     * @createdTime 2022/12/1 11:13
     */
    @PostMapping("/im/robot/callBack")
    void callBackMethod(RobotCallBackParam callBackParam);

    /**
     * 机器人自定义指令-送审文件回调
     * 通过送审文件的urlId获取发送消息需要的参数
     * @param req
     * @return string
     * @author zy.qiu
     * @createdTime 2023/1/11 18:15
     */
    @PostMapping("/im/robot/customCommand/getParamForSendMsg")
    RobotCustomCommandResp getParamForSendMsg(RobotCustomCommandReq req);

    /**
     * 机器人自定义指令-送审
     * @param req
     * @return List<MaterialSubmitResp>
     * @author zy.qiu
     * @createdTime 2023/1/31 09:15
     */
    @PostMapping(value = "/im/robot/customCommand/materialSubmit", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    MaterialSubmitResp materialSubmit(MaterialSubmitReq req);

    /**
     * 机器人自定义指令-查变量
     * @param req
     * @return string
     * @author zy.qiu
     * @createdTime 2023/4/18 18:15
     */
    @PostMapping("/im/robot/customCommand/variableEditByName")
    RobotCustomCommandResp variableEditByName(RobotCustomCommandReq req);


    /**
     * 机器人自定义指令-改变量
     * @param req
     * @return string
     * @author zy.qiu
     * @createdTime 2023/4/18 18:15
     */
    @PostMapping("/im/robot/customCommand/variableList")
    RobotCustomCommandResp variableList(RobotCustomCommandReq req);
}
