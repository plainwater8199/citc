package com.citc.nce.robot.api;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.robot.req.DeleteReq;
import com.citc.nce.robot.req.RobotGroupSendPlansDetailReq;
import com.citc.nce.robot.vo.PlanResp;
import com.citc.nce.robot.vo.RobotGroupSendPlansAndOperatorCode;
import com.citc.nce.robot.vo.RobotGroupSendPlansDetail;
import com.citc.nce.robot.vo.RobotGroupSendPlansDetailPageParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

@FeignClient(value = "im-service", contextId = "robotGroupSendPlansDetail", url = "${im:}")
public interface RobotGroupSendPlansDetailApi {
    /**
     * 通过ID查询单条数据
     *
     * @return 实例对象
     */
    @PostMapping("/group/detail/queryById")
    RobotGroupSendPlansDetail queryById(@RequestBody RobotGroupSendPlansDetailReq robotGroupSendPlansDetailReq);

    /**
     * 分页查询
     *
     * @param robotGroupSendPlansDetailPageParam      分页对象
     * @return 查询结果
     */
    @PostMapping("/group/detail/queryByPage")
    PageResult queryByPage(@RequestBody RobotGroupSendPlansDetailPageParam robotGroupSendPlansDetailPageParam);

    /**
     * 新增数据
     *
     * @param robotGroupSendPlansDetailReq 实例对象
     * @return 实例对象
     */
    @PostMapping("/group/detail/insert")
    Long insert(@RequestBody RobotGroupSendPlansDetailReq robotGroupSendPlansDetailReq);

    /**
     * 修改数据
     *
     * @param robotGroupSendPlansDetailReq 实例对象
     * @return 实例对象
     */
    @PostMapping("/group/detail/update")
    void update(@RequestBody @Valid RobotGroupSendPlansDetailReq robotGroupSendPlansDetailReq);

    /**
     * 通过主键删除数据
     *
     * @return 是否成功
     */
    @PostMapping("/group/detail/deleteById")
    void deleteById(@RequestBody RobotGroupSendPlansDetailReq robotGroupSendPlansDetailReq);

    @PostMapping("/group/detail/updateAmount")
    void updateAmount(@RequestBody RobotGroupSendPlansDetailReq robotGroupSendPlansDetailReq);

    @PostMapping("/group/detail/batchDelete")
    void batchDelete(@RequestBody DeleteReq deleteReq);

    @PostMapping("/group/detail/selectAllByPlanId")
    List<RobotGroupSendPlansDetail> selectAllByPlanId(@RequestBody RobotGroupSendPlansDetailReq req);

    @PostMapping("/group/detail/supplier/selectByTaskId")
    RobotGroupSendPlansAndOperatorCode selectByTaskId(@RequestParam("taskId") String taskId);

    @PostMapping("/group/detail/supplier/getByTaskIds")
    List<RobotGroupSendPlansAndOperatorCode> getByTaskIds(@RequestBody List<String> taskIds);

    /**
     * 查询所有待执行但当前时间大于设定的发送时间的
     * @return
     */
    @PostMapping("/group/detail/selectAllSchedule")
    List<Long> selectAllSchedule();

    /**
     * 更新数据库中数据为过期
     * @param ids id
     */
    @PostMapping("/group/detail/updateExpiredData")
    List<Long> updateExpiredData(@RequestBody List<Long> ids);

}
