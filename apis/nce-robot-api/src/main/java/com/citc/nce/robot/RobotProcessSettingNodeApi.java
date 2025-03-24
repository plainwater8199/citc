package com.citc.nce.robot;

import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.robot.vo.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/7/8 9:44
 * @Version: 1.0
 * @Description:
 */
@FeignClient(value = "rebot-service",contextId="RobotProcessSetting", url = "${robot:}")
public interface RobotProcessSettingNodeApi {
    /**
     * 流程管理列表获取
     *
     * @param
     * @return
     */
    @PostMapping("/process/list")
    PageResultResp getRobotProcessSettings(@RequestBody @Valid RobotProcessSettingNodePageReq robotProcessSettingNodePageReq);

    @PostMapping("/process/getOne")
    RobotProcessSettingNodeResp getRobotProcessOne(@RequestParam("id") @NotNull Long id) ;
        /**
         * 新增流程管理
         *
         * @param
         * @return
         */
    @PostMapping("/process/save")
    Long saveRobotProcessSetting(@RequestBody @Valid RobotProcessSettingNodeReq robotProcessSettingNodeReq);

    /**
     * 修改流程管理
     *
     * @param
     * @return
     */
    @PostMapping("/process/edit")
    int updateRobotProcessSetting(@RequestBody @Valid RobotProcessSettingNodeEditReq robotProcessSettingNodeEditReq);

    /**
     * 删除流程管理
     *
     * @param
     * @return
     */
    @PostMapping("/process/delete")
    int deleteRobotProcessSettingById(Long id);

    /**
     * 开关控制
     *
     * @param
     * @return
     */
    @PostMapping("/process/open")
    int updateRobotProcessSettingDerailById(@RequestBody @Valid RobotProcessSettingNodeEditDerailReq robotProcessSettingNodeEditDerailReq);

    /**
     * 获取单个流程管理
     *
     * @param
     * @return
     */
    @PostMapping("/process/getRobotProcessSettingById")
    RobotProcessSettingNodeResp getRobotProcessSettingById(@RequestParam("id") Long id);
}
