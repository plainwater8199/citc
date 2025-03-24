package com.citc.nce.developer.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.developer.entity.DeveloperSendDo;
import com.citc.nce.developer.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author ping chen
 */
@Mapper
public interface DeveloperSendMapper extends BaseMapper<DeveloperSendDo> {

    void createTable(String tableName);
    void dropTable(@Param("tableName") String tableName);

    Page<DeveloperCustomerVo> searchDeveloperSend(
            @Param("accountId") String accountId,
            @Param("customerUserId") String customerUserId,
            @Param("phone") String phone,
            @Param("callResult") Integer callResult,
            @Param("sendPlatformResult") Integer sendPlatformResult,
            @Param("callbackPlatformResult") Integer callbackPlatformResult,
            @Param("callbackResult") Integer callbackResult,
            @Param("callTimeStart") String callTimeStart,
            @Param("callTimeEnd") String callTimeEnd,
            @Param("type") Integer type,
            Page<DeveloperCustomerVo> page
    );

    List<DeveloperCllTrendVo> cllTrend(@Param("developerStatisticsTimeVo") DeveloperStatisticsTimeVo developerStatisticsTimeVo,@Param("customerUserId") String customerUserId,@Param("type") Integer type);

    List<DeveloperSendTrendByUserVo> sendTrendByUser(@Param("developerStatisticsTimeVo") DeveloperStatisticsTimeVo developerStatisticsTimeVo,@Param("customerUserId") String customerUserId,@Param("type") Integer type);

    List<DeveloperSendCountVo> queryCount(@Param("customerIds") List<String> customerIds,@Param("type") Integer type);

    List<DeveloperSendCountVo> queryCountAll(@Param("customerIds") List<String> customerIds,@Param("type") Integer type);

    List<DeveloperSend5gCountVo> query5gCount(@Param("customerIds") List<String> customerIds,@Param("type") Integer type);

    List<DeveloperSend5gCountVo> query5gCountAll(@Param("customerIds")List<String> customerIds, @Param("type") Integer type);


}
