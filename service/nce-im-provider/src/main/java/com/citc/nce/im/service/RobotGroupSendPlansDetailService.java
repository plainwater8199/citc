package com.citc.nce.im.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.im.entity.RobotGroupSendPlansDetailDo;
import com.citc.nce.robot.req.RobotGroupSendPlansDetailReq;
import com.citc.nce.robot.vo.RobotGroupSendPlansDetail;
import com.citc.nce.robot.vo.RobotGroupSendPlansDetailPageParam;

import java.util.List;
import java.util.Map;


public interface RobotGroupSendPlansDetailService extends IService<RobotGroupSendPlansDetailDo> {
    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    public RobotGroupSendPlansDetail queryById(Long id);

    /**
     * 分页查询
     *
     * @param robotGroupSendPlansDetailPageParam      分页对象
     * @return 查询结果
     */
    public PageResult queryByPage(RobotGroupSendPlansDetailPageParam robotGroupSendPlansDetailPageParam);

    /**
     * 新增数据
     *
     * @param robotGroupSendPlansDetailReq 实例对象
     * @return 实例对象
     */
    public Long insert(RobotGroupSendPlansDetailReq robotGroupSendPlansDetailReq);

    /**
     * 修改数据
     *
     * @param robotGroupSendPlansDetailReq 实例对象
     * @return 实例对象
     */
    public void updateNodeDetail(RobotGroupSendPlansDetailReq robotGroupSendPlansDetailReq);

    /**
     * 修改数据
     *
     * @param robotGroupSendPlansDetailReq 实例对象
     * @return 实例对象
     */
    public void updateDate(RobotGroupSendPlansDetailReq robotGroupSendPlansDetailReq);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    public void deleteById(Long id);

    void updateAmount(RobotGroupSendPlansDetailReq robotGroupSendPlansDetailReq);

    List<RobotGroupSendPlansDetail> selectAllByPlanId(RobotGroupSendPlansDetailReq req);

    List<Long> updateExpiredData(List<Long> ids);

    List<Long> selectAllSchedule();

    Map<Long, Long> sumSendAmountByPlanId();



}
