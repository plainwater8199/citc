package com.citc.nce.robot.api;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.robot.req.RobotGroupSendPlanDescContainShortUrlReq;
import com.citc.nce.robot.req.RobotGroupSendPlanDescReq;
import com.citc.nce.robot.vo.MediaAccountResp;
import com.citc.nce.robot.vo.RobotGroupSendPlanDesc;
import com.citc.nce.robot.vo.RobotGroupSendPlanDescPageParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "im-service", contextId = "robotGroupSendPlanDesc" ,url = "${im:}")
public interface RobotGroupSendPlanDescApi {
    /**
     * 通过ID查询单条数据
     *
     * @return 实例对象
     */
    @PostMapping("/group/desc/queryById")
    RobotGroupSendPlanDesc queryById(@RequestBody RobotGroupSendPlanDescReq robotGroupSendPlanDescReq);

    /**
     * 分页查询
     *
     * @param robotGroupSendPlanDescPageParam      分页对象
     * @return 查询结果
     */
    @PostMapping("/group/desc/queryByPage")
    PageResult queryByPage(@RequestBody RobotGroupSendPlanDescPageParam robotGroupSendPlanDescPageParam);

    /**
     * 新增数据
     *
     * @param robotGroupSendPlanDescReq 实例对象
     * @return 实例对象
     */
    @PostMapping("/group/desc/insert")
    void insert(@RequestBody RobotGroupSendPlanDescReq robotGroupSendPlanDescReq);

    /**
     * 修改数据
     *
     * @param robotGroupSendPlanDescReq 实例对象
     * @return 实例对象
     */
    @PostMapping("/group/desc/update")
    void update(@RequestBody RobotGroupSendPlanDescReq robotGroupSendPlanDescReq);

    /**
     * 通过主键删除数据
     *
     * @return 是否成功
     */
    @PostMapping("/group/desc/deleteById")
    void deleteById(@RequestBody RobotGroupSendPlanDescReq robotGroupSendPlanDescReq);

    /**
     * 通过主键删除数据
     *
     * @return 是否成功
     */
    @PostMapping("/group/desc/queryPlanAccount")
    MediaAccountResp queryPlanAccount(@RequestBody RobotGroupSendPlanDescReq robotGroupSendPlanDescReq);

    /**
     * 查询customer名下的群发计划中是否包含某短链
     *
     * @return 实例对象
     */
    @PostMapping("/group/desc/containList")
    boolean containList(@RequestBody RobotGroupSendPlanDescContainShortUrlReq req);

    /**
     * 查询customer名下的群发计划中是否包含某5G阅信
     *
     * @return 实例对象
     */
    @PostMapping("/group/desc/containOne")
    boolean containOne(@RequestParam("one") String one);
}
