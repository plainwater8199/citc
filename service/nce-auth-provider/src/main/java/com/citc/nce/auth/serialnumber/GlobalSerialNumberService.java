package com.citc.nce.auth.serialnumber;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.serialnumber.dao.GlobalUniqueSerialNumberMapper;
import com.citc.nce.common.core.exception.BizException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @author jcrenc
 * @since 2024/5/23 14:56
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GlobalSerialNumberService extends ServiceImpl<GlobalUniqueSerialNumberMapper, GlobalUniqueSerialNumber> implements IService<GlobalUniqueSerialNumber> {
    /**
     * 申请全局唯一的序列号（通过数据库唯一索引保证）
     *
     * @param type         类型，不能为空，不同类型下可以重复
     * @param serialNumber 序列号，最大长度支持255个字符
     */
    @Transactional(rollbackFor = Exception.class)
    public void applyGlobalUniqueSerialNumber(Integer type, String serialNumber) {
       Assert.notNull(type, "交易流水号类型不能为空");
        Assert.isTrue(StringUtils.isNotBlank(serialNumber), "交易流水号不能为空或者空字符串");
        Assert.isTrue(StringUtils.isNumeric(serialNumber), "交易流水号必须为纯数字组成");
        Assert.isTrue(serialNumber.length()<=32, "交易流水号长度不能超过32位");
        GlobalUniqueSerialNumber setSerialNumber = new GlobalUniqueSerialNumber()
                .setType(type)
                .setSerialNumber(serialNumber);
        try {
            save(setSerialNumber);
        } catch (DuplicateKeyException e) {
            log.warn("类型:{} 序列号:{} 重复, msg:{}", type, serialNumber, e.getMessage());
            throw new BizException("交易流水号重复");
        }
    }
}
