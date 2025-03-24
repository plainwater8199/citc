package com.citc.nce.im.massSegment.controller;


import com.citc.nce.im.massSegment.service.IMassSegmentService;
import com.citc.nce.robot.api.MassSegmentApi;
import com.citc.nce.robot.domain.massSegment.MassSegmentDetail;
import com.citc.nce.robot.domain.massSegment.MassSegmentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 运营商号段关系表 前端控制器
 * </p>
 *
 * @author bydud
 * @since 2024-05-06 04:05:59
 */
@RestController
public class MassSegmentController implements MassSegmentApi {

    @Autowired
    private IMassSegmentService service;

    @Override
    public Map<String, List<MassSegmentDetail>> listCustomGroupOperator() {
        return service.listGroupOperator(null);
    }

    @Override
    public String queryOperator(String phoneSegment) {
        return service.getOperatorStringByPhone(phoneSegment);
    }

    @Override
    public Map<String, String> queryAllSegment() {
        return service.queryAllSegment();
    }

    @Override
    public void saveCustom(MassSegmentVo entity) {
        service.saveCustom(entity);
    }

    @Override
    public void removeById(Long id) {
        service.delById(id);
    }
}

