package com.citc.nce.auth.cardstyle.dao;

import com.citc.nce.auth.cardstyle.entity.CardStyleDo;
import com.citc.nce.auth.contactbacklist.entity.ContactBackListDo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Update;

import java.util.HashMap;

/**
 * 卡片样式
 */
public interface CardStyleDao extends BaseMapperX<CardStyleDo> {

    @Update("UPDATE card_style SET delete_time=#{deleteTime},deleted=#{deleted} WHERE id=#{id}")
    int delCardStyleById(HashMap<String, Object> map);
}
