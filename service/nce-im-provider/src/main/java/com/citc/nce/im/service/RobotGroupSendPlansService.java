package com.citc.nce.im.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.im.entity.RobotGroupSendPlansDo;
import com.citc.nce.robot.req.RobotGroupSendPlansByPlanNameReq;
import com.citc.nce.robot.req.RobotGroupSendPlansReq;
import com.citc.nce.robot.vo.RobotGroupSendPlans;
import com.citc.nce.robot.vo.RobotGroupSendPlansAndChatbotAccount;
import com.citc.nce.robot.vo.RobotGroupSendPlansPageParam;


import java.util.List;
import java.util.Map;

public interface RobotGroupSendPlansService extends IService<RobotGroupSendPlansDo> {
    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    public RobotGroupSendPlans queryById(Long id);

    /**
     * 分页查询
     *
     * @param robotGroupSendPlansPageParam      分页对象
     * @return 查询结果
     */
    public PageResult queryByPage(RobotGroupSendPlansPageParam robotGroupSendPlansPageParam);

    /**
     * 新增数据
     *
     * @param robotGroupSendPlansReq 实例对象
     * @return 实例对象
     */
    public Long insert(RobotGroupSendPlansReq robotGroupSendPlansReq);

    /**
     * 移除客户下所有群发计划关联账号包含了chatbot账号的chatbot信息
     * @param chatbotAccount
     * @param accountType
     */
    public void removeChatbotAccount(String chatbotAccount,String accountType,String planChatbotAccountSupplier);

    /**
     * 修改数据
     *
     * @param robotGroupSendPlansReq 实例对象
     * @return 实例对象
     */
    public void update(RobotGroupSendPlansReq robotGroupSendPlansReq);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    public void deleteById(Long id);

    /**
     * 查找出所有复合状态的模板id
     * @return
     */
    List<Long> selectAllPlanIds();

    List<Long> selectIdsByStatus(RobotGroupSendPlansReq robotGroupSendPlansReq);

    List<RobotGroupSendPlansAndChatbotAccount> getByGroupSendIds(List<String> selectPlanIds);

    List<RobotGroupSendPlansAndChatbotAccount> selectByPlanName(RobotGroupSendPlansByPlanNameReq req);

    String selectPlanChatbotAccount(RobotGroupSendPlansReq robotGroupSendPlansReq);
}
