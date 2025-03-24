package com.citc.nce.customcommand.service;

import com.citc.nce.customcommand.entity.CustomCommandRequirementOperationLog;
import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.customcommand.enums.CustomCommandRequirementOperation;

/**
 * <p>
 * 定制需求管理(自定义指令)操作日志 服务类
 * </p>
 *
 * @author jcrenc
 * @since 2023-11-09 02:53:48
 */
public interface ICustomCommandRequirementOperationLogService extends IService<CustomCommandRequirementOperationLog> {

    /**
     * 需求操作记录
     *
     * @param requirementId 需求ID
     * @param operation     操作类型
     * @param note          备注
     */
    void operationRecord(Long requirementId, CustomCommandRequirementOperation operation, String note);

}
