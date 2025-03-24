package com.citc.nce.module.dao;

import com.citc.nce.dataStatistics.dto.MsgStatisticsDto;
import com.citc.nce.module.entity.SubscribeContentDo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SubscribeContentDao extends BaseMapperX<SubscribeContentDo> {

    SubscribeContentDo querySubScribeContent(@Param("subscribeContentId") String subscribeContentId);

    List<SubscribeContentDo> findListById(@Param("subscribeId") String subscribeId);
}
