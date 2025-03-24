package com.citc.nce.customcommand.service;

import com.citc.nce.customcommand.entity.CustomCommandOperationLog;
import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.customcommand.enums.CustomCommandOperation;

/**
 * <p>
 * 自定义指令操作日志 服务类
 * </p>
 *
 * @author jcrenc
 * @since 2023-11-09 02:53:48
 */
public interface ICustomCommandOperationLogService extends IService<CustomCommandOperationLog> {

    /**
     * 指令生产操作记录
     *
     * @param id        指令ID
     * @param uuid      指令uuid
     * @param operation 操作类型
     */
    void operationRecord(Long id, String uuid, CustomCommandOperation operation);

}
