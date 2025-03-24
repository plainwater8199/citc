package com.citc.nce.customcommand.service.impl;

import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.customcommand.entity.CustomCommandOperationLog;
import com.citc.nce.customcommand.dao.CustomCommandOperationLogMapper;
import com.citc.nce.customcommand.enums.CustomCommandOperation;
import com.citc.nce.customcommand.service.ICustomCommandOperationLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * <p>
 * 自定义指令操作日志 服务实现类
 * </p>
 *
 * @author jcrenc
 * @since 2023-11-09 02:53:48
 */
@Service
public class CustomCommandOperationLogServiceImpl extends ServiceImpl<CustomCommandOperationLogMapper, CustomCommandOperationLog> implements ICustomCommandOperationLogService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void operationRecord(Long id, String uuid, CustomCommandOperation operation) {
        BaseUser user = SessionContextUtil.getUser();
        CustomCommandOperationLog operationLog = new CustomCommandOperationLog()
                .setCustomCommandId(id)
                .setCustomCommandUuid(uuid)
                .setOperation(operation)
                .setOperatorName(user.getUserName())
                .setOperatorId(user.getUserId())
                .setOperateTime(LocalDateTime.now());
        this.save(operationLog);
    }
}
