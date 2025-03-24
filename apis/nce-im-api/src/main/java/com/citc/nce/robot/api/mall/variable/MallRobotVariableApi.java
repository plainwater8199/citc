package com.citc.nce.robot.api.mall.variable;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.robot.api.mall.variable.vo.req.MallRobotVariableDeleteReq;
import com.citc.nce.robot.api.mall.variable.vo.req.MallRobotVariableDetailReq;
import com.citc.nce.robot.api.mall.variable.vo.req.MallRobotVariableQueryListReq;
import com.citc.nce.robot.api.mall.variable.vo.req.MallRobotVariableSaveReq;
import com.citc.nce.robot.api.mall.variable.vo.req.MallRobotVariableUpdateReq;
import com.citc.nce.robot.api.mall.variable.vo.resp.MallRobotVariableDetailResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>扩展商城-模板</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/11/21 15:35
 */
@FeignClient(value = "im-service",contextId="MallRobotVariableApi", url = "${im:}")
public interface MallRobotVariableApi {

    @PostMapping("/mall/robotVariable/save")
    int save(@RequestBody @Valid MallRobotVariableSaveReq req);
    @PostMapping("/mall/robotVariable/update")
    int update(@RequestBody @Valid MallRobotVariableUpdateReq req);
    @PostMapping("/mall/robotVariable/delete")
    int delete(@RequestBody @Valid MallRobotVariableDeleteReq req);
    @PostMapping("/mall/robotVariable/queryDetail")
    MallRobotVariableDetailResp queryDetail(@RequestBody @Valid MallRobotVariableDetailReq req);
    @PostMapping("/mall/robotVariable/queryList")
    PageResult<MallRobotVariableDetailResp> queryList(@RequestBody MallRobotVariableQueryListReq req);
    @PostMapping("/mall/robotVariable/queryListByIds")
    List<MallRobotVariableDetailResp> listByIdsDel(@RequestBody List<String> ids);

}
