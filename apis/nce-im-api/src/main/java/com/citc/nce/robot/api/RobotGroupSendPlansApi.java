package com.citc.nce.robot.api;

import com.citc.nce.common.core.pojo.PageResult;

import com.citc.nce.robot.req.RobotGroupSendPlanAccountReq;
import com.citc.nce.robot.req.RobotGroupSendPlanIdReq;
import com.citc.nce.robot.req.RobotGroupSendPlansByPlanNameReq;
import com.citc.nce.robot.req.RobotGroupSendPlansReq;
import com.citc.nce.robot.vo.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(value = "im-service", contextId = "robotGroupSendPlans", url = "${im:}")
public interface RobotGroupSendPlansApi {
    /**
     * 通过ID查询单条数据
     *
     * @return 实例对象
     */
    @PostMapping("/group/plans/queryById")
    RobotGroupSendPlans queryById(@RequestBody RobotGroupSendPlansReq robotGroupSendPlansReq);

    @PostMapping("/group/plans/queryByIds")
    List<RobotGroupSendPlansAndChatbotAccount> getByGroupSendIds(@RequestBody List<String> selectPlanIds);

    @PostMapping("/group/plans/selectByPlanName")
    List<RobotGroupSendPlansAndChatbotAccount> selectByPlanName(@RequestBody RobotGroupSendPlansByPlanNameReq req);


    /**
     * 分页查询
     *
     * @param robotGroupSendPlansPageParam      分页对象
     * @return 查询结果
     */
    @PostMapping("/group/plans/queryByPage")
    PageResult queryByPage(@RequestBody RobotGroupSendPlansPageParam robotGroupSendPlansPageParam);

    /**
     * 新增数据
     *
     * @param robotGroupSendPlansReq 实例对象
     * @return 实例对象
     */
    @PostMapping("/group/plans/insert")
    Long insert(@RequestBody RobotGroupSendPlansReq robotGroupSendPlansReq);

    /**
     * 修改数据
     *
     * @param robotGroupSendPlansReq 实例对象
     * @return 实例对象
     */
    @PostMapping("/group/plans/update")
    void update(@RequestBody RobotGroupSendPlansReq robotGroupSendPlansReq);

    /**
     * 通过主键删除数据
     *
     * @return 是否成功
     */
    @PostMapping("/group/plans/deleteById")
    void deleteById(@RequestBody RobotGroupSendPlansReq robotGroupSendPlansReq);

    /**
     * 检查计划是否有使用模板
     *
     *  * 1 联系人组
     *      * 2 模板
     * @return 是否成功
     */
    @PostMapping("/group/plans/checkPlansUserSourceId")
    Boolean checkPlansUserTemplate(@RequestBody RobotGroupSendPlanIdReq robotGroupSendPlanIdReq);

    /**
     * 检查计划是否有使用账号
     *
     * @return 是否成功
     */
    @PostMapping("/group/plans/checkPlansUserAccount")
    Boolean checkPlansUserAccount(@RequestBody RobotGroupSendPlanAccountReq robotGroupSendPlanAccountReq);

    @PostMapping("/group/plans/selectIdsByStatus")
    List<Long> selectIdsByStatus(@RequestBody RobotGroupSendPlansReq robotGroupSendPlansReq);


    /**
     * 查找所有复合状态的模板id
     * @return 模板id
     */
    @PostMapping("/group/plans/selectAllPlanIds")
    List<Long> selectAllPlanIds();

    @PostMapping("/group/plans/selectAll")
    List<PlanResp> selectAll();

    //查询计划对应的账号PlanChatbotAccount
    @PostMapping("/group/plans/selectPlanChatbotAccount")
    String selectPlanChatbotAccount(@RequestBody RobotGroupSendPlansReq robotGroupSendPlansReq);

    /**
     * 移除客户下所有群发计划关联账号包含了chatbot账号的chatbot信息
     * @param chatbotAccount
     * @param accountType
     */
    @GetMapping("/group/plans/removeChatbotAccount")
    public void removeChatbotAccount(@RequestParam("chatbotAccount") String chatbotAccount,@RequestParam("accountType")String accountType,@RequestParam("planChatbotAccountSupplier")String planChatbotAccountSupplier);
}
