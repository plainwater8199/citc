package com.citc.nce.misc.dictionary.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.misc.dictionary.entity.BaseArea;
import com.citc.nce.misc.dictionary.vo.req.BaseAreaReq;
import com.citc.nce.misc.dictionary.vo.resp.BaseAreaResp;

import java.util.List;

/**
 * <p>
 * 统一地区库 服务类
 * </p>
 *
 * @author author
 * @since 2023-02-07
 */
public interface IBaseAreaService extends IService<BaseArea> {

    List<BaseAreaResp> findRegion();

    List<BaseAreaResp> findByParentId(BaseAreaReq areaReq);

    List<BaseAreaResp> findIndustry();
}
