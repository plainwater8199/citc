package com.citc.nce.robot;

import com.citc.nce.robot.vo.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

@FeignClient(value = "rebot-service",contextId="RobotAccountApi", url = "${robot:}")
public interface RobotAccountApi {

    /**
     * 查询聊天会话账户
     *
     * @return
     */
    @PostMapping("/robot/account/pagelist")
    RobotAccountPageResultResp getRobotAccountList(@RequestBody @Valid RobotAccountPageReq robotAccountPageReq);

    /**
     * 新增聊天会话账户
     *
     * @return
     */
    @PostMapping("/robot/account/save")
    int saveRobotAccount(@RequestBody @Valid RobotAccountReq robotAccountReq);

}
