package com.citc.nce.misc.dictionary.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.citc.nce.misc.dictionary.entity.BaseArea;
import com.citc.nce.misc.dictionary.vo.req.BaseAreaReq;
import com.citc.nce.misc.dictionary.vo.resp.BaseAreaResp;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BaseAreaMapper extends BaseMapper<BaseArea> {

    List<BaseAreaResp> findRegion();

    List<BaseAreaResp> findIndustry();

    List<BaseAreaResp> findByParentId(BaseAreaReq areaReq);
}
