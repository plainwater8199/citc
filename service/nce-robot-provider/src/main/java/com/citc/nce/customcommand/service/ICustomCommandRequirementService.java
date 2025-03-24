package com.citc.nce.customcommand.service;

import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.customcommand.entity.CustomCommandRequirement;
import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.customcommand.vo.*;

/**
 * <p>
 * 定制需求管理(自定义指令) 服务类
 * </p>
 *
 * @author jcrenc
 * @since 2023-11-09 02:53:48
 */
public interface ICustomCommandRequirementService extends IService<CustomCommandRequirement> {
    /**
     * 新增定制需求
     *
     * @param addReq
     */
    void add(CustomCommandRequirementAddReq addReq);

    /**
     * 获取待处理需求数量
     *
     * @return
     */
    Long countWaitRequirement();

    /**
     * 搜索定制需求
     */
    PageResult<CustomCommandRequirementSimpleVo> searchCustomCommandRequirement(CustomCommandRequirementSearchReq searchReq);

    /**
     * 查询需求详情
     * @param requirementId 需求ID
     */
    CustomCommandRequirementDetailVo getRequirementDetail(Long requirementId);

    /**
     * 修改需求沟通记录
     * @param id 需求ID
     * @param note 沟通记录
     */
    void updateRequirementNote(Long id, String note);

    /**
     * 处理需求
     */
    void processRequirement(CustomCommandRequirementProcessReq processReq);

    /**
     * 查询我的需求列表
     * @param pageParam 分页参数
     */
    PageResult<CustomCommandRequirementSimpleVo> getMyRequirements(PageParam pageParam);
}
