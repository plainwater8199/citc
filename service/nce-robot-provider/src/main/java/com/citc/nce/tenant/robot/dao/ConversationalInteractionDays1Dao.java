package com.citc.nce.tenant.robot.dao;


import com.citc.nce.tenant.robot.entity.ConversationalInteractionDaysDo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 每天会话用户统计表 Mapper 接口
 * </p>
 *
 * @author author
 * @since 2022-10-27
 */
@Mapper
public interface ConversationalInteractionDays1Dao extends BaseMapperX<ConversationalInteractionDaysDo> {
    /**
     * getConversationalInteractionInWeek
     * @param map
     * @return list
     * @author zy.qiu
     * @createdTime 2022/12/6 17:07
     */
    List<ConversationalInteractionDaysDo> getConversationalInteractionInWeek(HashMap<String, Object> map);


}
