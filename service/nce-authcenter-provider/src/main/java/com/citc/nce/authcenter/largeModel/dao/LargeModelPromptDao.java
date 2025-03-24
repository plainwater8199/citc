package com.citc.nce.authcenter.largeModel.dao;

import com.citc.nce.authcenter.largeModel.entity.LargeModelPromptDo;
import com.citc.nce.authcenter.largeModel.vo.PromptDetailResp;
import com.citc.nce.authcenter.largeModel.vo.PromptReq;
import com.citc.nce.authcenter.largeModel.vo.PromptResp;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LargeModelPromptDao extends BaseMapperX<LargeModelPromptDo> {
    List<PromptResp> queryList(PromptReq req);

    Long queryListCount(PromptReq req);

    PromptDetailResp queryDetail(Long id);
}
