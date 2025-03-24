package com.citc.nce.developer.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.citc.nce.developer.entity.DeveloperSendStatisticsDo;
import com.citc.nce.developer.vo.*;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author ping chen
 */
public interface DeveloperSendStatisticsMapper extends BaseMapperX<DeveloperSendStatisticsDo> {

    void createTable(String tableName);

    void dropTable(@Param("tableName") String tableName);

    List<DeveloperCllTrendVo> cllTrend(@Param("developerStatisticsTimeVo") DeveloperStatisticsTimeVo developerStatisticsTimeVo, @Param("customerUserId") String customerUserId,@Param("type") Integer type);

    DeveloperYesterdayOverviewVo yesterdayOverview(@Param("time") String time,@Param("customerUserId") String customerUserId,@Param("type") Integer type);

    DeveloperCllAnalysisVo callAnalysis(@Param("customerUserId") String customerUserId, @Param("developerStatisticsTimeVo") DeveloperStatisticsTimeVo developerStatisticsTimeVo,@Param("type") Integer type);

    DeveloperSendAnalysisVo sendAnalysis(@Param("customerUserId") String customerUserId,@Param("developerStatisticsTimeVo") DeveloperStatisticsTimeVo developerStatisticsTimeVo,@Param("type") Integer type);

    List<DeveloperSendTrendByUserVo> sendTrendByUser(@Param("developerStatisticsTimeVo") DeveloperStatisticsTimeVo developerStatisticsTimeVo, @Param("customerUserId") String customerUserId,@Param("type") Integer type);

    List<DeveloperApplication5gRankingVo> applicationRanking(@Param("developerStatisticsTimeVo") DeveloperStatisticsTimeVo developerStatisticsTimeVo, @Param("customerUserId") String customerUserId);

    List<DeveloperSendCountVo> queryCount(@Param("customerIds") List<String> customerIds,@Param("type") Integer type);

    List<DeveloperSendCountVo> queryCountAll(@Param("customerIds") List<String> customerIds,@Param("type") Integer type);

    List<DeveloperSend5gCountVo> queryCount5gAll(@Param("customerIds") List<String> customerIds,@Param("type") Integer type);

    List<DeveloperSendStatisticsVo> queryDeveloperSendCount(String time);

    List<DeveloperSendStatisticsVo> queryDeveloperSendCallCount(String time);

    List<String> select5gCustomerYesterday(String time);

    List<String> select5gCustomer();

    List<String> select5gCustomerNotDeletedYesterday(String time);

    List<String> select5gCustomerNotDeleted();

    List<String> selectSmsCustomerYesterday(String time);

    List<String> selectSmsCustomer();

    List<String> selectVideoCustomerYesterday(String time);

    List<String> selectVideoCustomer();

    Integer selectSendCustomerYesterday(String time);


    Long select5GApplicationCount();
}
