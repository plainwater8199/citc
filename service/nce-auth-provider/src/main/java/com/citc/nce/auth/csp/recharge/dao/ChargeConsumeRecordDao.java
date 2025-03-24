package com.citc.nce.auth.csp.recharge.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.citc.nce.auth.csp.recharge.entity.ChargeConsumeRecord;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author yy
 * @date 2024-10-22 16:12:41
 */
public interface ChargeConsumeRecordDao extends BaseMapper<ChargeConsumeRecord> {
    void createTableIfNotExist(String tableName);

    void dropTable(@Param("tableName") String tableName);

    void clearTable(@Param("tableName") String tableName);

    void updateProcessStatus(@Param("ids") List<Long> ids, @Param("tableName") String tableName, @Param("code") Integer code);

    List<ChargeConsumeRecord> getChargeConsumeRecordNeedThaw( @Param("msgType")Integer msgType, @Param("tableName") String cspId,@Param("tariffTypes") List<Integer> tariffTypes, @Param("date") Date date);
}
