package com.citc.nce.dataStatistics.dao;

import com.citc.nce.dataStatistics.entity.ProcessQuantityDaysDo;
import com.citc.nce.dataStatistics.vo.resp.ProcessQuantityAllResp;
import com.citc.nce.dataStatistics.vo.resp.ProcessQuantityResp;
import com.citc.nce.dataStatistics.vo.resp.ProcessQuantityTableResp;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 每日流程统计表 Mapper 接口
 * </p>
 *
 * @author author
 * @since 2022-10-27
 */
@Mapper
public interface ProcessQuantityDaysDao extends BaseMapperX<ProcessQuantityDaysDo> {

    ProcessQuantityResp getProcessQuantityResp(HashMap<String, Object> map);

    List<ProcessQuantityAllResp> getProcessQuantityYesterdays(HashMap<String, Object> map);
    /**
     * getProcessQuantityDaysInWeek
     * @param map
     * @return ProcessQuantityDaysDao
     * @author zy.qiu
     * @createdTime 2022/12/6 10:37
     */
    List<ProcessQuantityDaysDo> getProcessQuantityDaysInWeek(HashMap<String, Object> map);

    List<ProcessQuantityTableResp> getProcessQuantityTableResps(HashMap<String, Object> map);

    List<ProcessQuantityTableResp> getProcessQuantityNum(HashMap<String, Object> map);
}
