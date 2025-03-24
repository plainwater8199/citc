package com.citc.nce.misc.dictionary.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.misc.dictionary.entity.BaseArea;
import com.citc.nce.misc.dictionary.mapper.BaseAreaMapper;
import com.citc.nce.misc.dictionary.service.IBaseAreaService;
import com.citc.nce.misc.dictionary.vo.req.BaseAreaReq;
import com.citc.nce.misc.dictionary.vo.resp.BaseAreaResp;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class BaseAreaServiceImpl extends ServiceImpl<BaseAreaMapper, BaseArea> implements IBaseAreaService {

    @Resource
    private BaseAreaMapper baseAreaMapper;


    @Override
    public List<BaseAreaResp> findRegion() {
        return baseAreaMapper.findRegion();
    }

    @Override
    public List<BaseAreaResp> findByParentId(BaseAreaReq areaReq) {
        return baseAreaMapper.findByParentId(areaReq);
    }

    @Override
    public List<BaseAreaResp> findIndustry() {
        return baseAreaMapper.findIndustry();
    }
}
