package com.citc.nce.dataStatistics.dao;

import com.citc.nce.dataStatistics.entity.ProcessQuantityWeeksDo;
import com.citc.nce.dataStatistics.vo.resp.ProcessQuantityAllResp;
import com.citc.nce.dataStatistics.vo.resp.ProcessQuantityResp;
import com.citc.nce.dataStatistics.vo.resp.ProcessQuantityTableResp;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 每周流程统计表 Mapper 接口
 * </p>
 *
 * @author author
 * @since 2022-10-27
 */
@Mapper
public interface ProcessQuantityWeeksDao extends BaseMapperX<ProcessQuantityWeeksDo> {

    ProcessQuantityResp getProcessQuantityResp(HashMap<String, Object> map);

    List<ProcessQuantityAllResp> getProcessQuantityYesterdays(HashMap<String, Object> map);

    List<ProcessQuantityTableResp> getProcessQuantityTableResps(HashMap<String, Object> map);

    List<ProcessQuantityTableResp> getProcessQuantityNum(HashMap<String, Object> map);
}
