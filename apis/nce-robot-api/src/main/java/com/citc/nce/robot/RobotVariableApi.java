package com.citc.nce.robot;


import com.citc.nce.robot.bean.RobotVariableBean;
import com.citc.nce.robot.vo.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@FeignClient(value = "rebot-service",contextId="variable-service", url = "${robot:}")
public interface RobotVariableApi {


    @PostMapping("/robot/variable/save")
    public void save(@RequestBody RobotVariableReq robotVariableReq);

    @PostMapping("/robot/variable/list")
    public RobotVariableResp listAll(@RequestBody RobotVariablePageReq robotVariablePageReq);
    @PostMapping("/robot/variable/queryByTsOrderId")
    List<RobotVariableBean> queryByTsOrderId(@RequestBody RobotVariableReq req);

    @PostMapping("/robot/variable/edit")
    public void compile(@RequestBody RobotVariableReq robotVariableReq);

    @PostMapping("/robot/variable/delete")
    public void removeVariable(@RequestBody RobotVariableReq robotVariableReq);

    @PostMapping("/robot/variable/queryById")
    public RobotVariableBean queryById(@RequestBody RobotVariableReq robotVariableReq);

    @PostMapping("/robot/variable/getlist")
    public RobotVariableResp getList(@RequestBody RobotVariableCreateReq robotVariableCreateReq);

    @PostMapping("/robot/variable/editByName")
    public RobotValueResetResp editByName(@RequestBody RobotVariableReq robotVariableReq);

}
