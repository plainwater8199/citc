package com.citc.nce.customcommand.service.impl;

import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.customcommand.entity.CustomCommandRequirementOperationLog;
import com.citc.nce.customcommand.dao.CustomCommandRequirementOperationLogMapper;
import com.citc.nce.customcommand.enums.CustomCommandRequirementOperation;
import com.citc.nce.customcommand.service.ICustomCommandRequirementOperationLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * <p>
 * 定制需求管理(自定义指令)操作日志 服务实现类
 * </p>
 *
 * @author jcrenc
 * @since 2023-11-09 02:53:48
 */
@Service
public class CustomCommandRequirementOperationLogServiceImpl extends ServiceImpl<CustomCommandRequirementOperationLogMapper, CustomCommandRequirementOperationLog> implements ICustomCommandRequirementOperationLogService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void operationRecord(Long requirementId, CustomCommandRequirementOperation operation, String note) {
        BaseUser user = SessionContextUtil.getUser();
        CustomCommandRequirementOperationLog operationLog = new CustomCommandRequirementOperationLog();
        operationLog.setRequirementId(requirementId)
                .setOperation(operation)
                .setNote(note)
                .setOperatorName(user.getUserName())
                .setOperatorId(user.getUserId())
                .setOperateTime(LocalDateTime.now());
        this.save(operationLog);
    }
}
