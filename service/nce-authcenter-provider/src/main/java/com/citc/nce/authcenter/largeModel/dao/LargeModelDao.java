package com.citc.nce.authcenter.largeModel.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.auth.csp.chatbot.vo.ChatbotResp;
import com.citc.nce.authcenter.largeModel.entity.LargeModelDo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LargeModelDao extends BaseMapperX<LargeModelDo> {
}
