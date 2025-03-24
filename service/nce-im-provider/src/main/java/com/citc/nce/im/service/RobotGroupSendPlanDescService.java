package com.citc.nce.im.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.im.entity.RobotGroupSendPlanDescDo;
import com.citc.nce.robot.req.RobotGroupSendPlanDescReq;
import com.citc.nce.robot.vo.RobotGroupSendPlanDesc;
import com.citc.nce.robot.vo.RobotGroupSendPlanDescPageParam;

import java.util.List;

public interface RobotGroupSendPlanDescService extends IService<RobotGroupSendPlanDescDo> {
    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    public RobotGroupSendPlanDesc queryById(Long id);

    /**
     * 分页查询
     *
     * @param robotGroupSendPlanDescPageParam      分页对象
     * @return 查询结果
     */
    public PageResult queryByPage(RobotGroupSendPlanDescPageParam robotGroupSendPlanDescPageParam);

    /**
     * 新增数据
     *
     * @param robotGroupSendPlanDescReq 实例对象
     * @return 实例对象
     */
    public void insert(RobotGroupSendPlanDescReq robotGroupSendPlanDescReq);

    /**
     * 修改数据
     *
     * @param robotGroupSendPlanDescReq 实例对象
     * @return 实例对象
     */
    public void update(RobotGroupSendPlanDescReq robotGroupSendPlanDescReq);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    public void deleteById(Long id);

    boolean containList(List<String> list);

    boolean containOne(String one);
}
