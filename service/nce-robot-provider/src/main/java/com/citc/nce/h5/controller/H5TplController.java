package com.citc.nce.h5.controller;


import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.h5.H5TplApi;
import com.citc.nce.h5.service.H5TplService;
import com.citc.nce.h5.vo.H5TplInfo;
import com.citc.nce.h5.vo.req.H5TplQueryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class H5TplController implements H5TplApi {

    @Autowired
    private H5TplService h5TplService;

    @Override
    public void create(H5TplInfo tpl) {
        h5TplService.create(tpl);
    }

    @Override
    public H5TplInfo previewData(Long id) {
        return h5TplService.previewData(id);
    }

    @Override
    public PageResult<H5TplInfo> page(H5TplQueryVO h5Vo) {
        return h5TplService.page(h5Vo);
    }

    @Override
    public void update(H5TplInfo h5Info) {
        h5TplService.update(h5Info);
    }

    @Override
    public void delete(Long id) {
        h5TplService.delete(id);
    }
}
