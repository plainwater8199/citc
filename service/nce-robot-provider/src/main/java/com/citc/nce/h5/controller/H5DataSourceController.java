package com.citc.nce.h5.controller;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.h5.H5DataSourceApi;
import com.citc.nce.h5.service.H5DataSourceService;
import com.citc.nce.h5.vo.H5DataSourceInfo;
import com.citc.nce.h5.vo.req.H5DataSourceQueryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
public class H5DataSourceController implements H5DataSourceApi {
    @Autowired
    private H5DataSourceService h5DataSourceService;



    @Override
    public PageResult<H5DataSourceInfo> page(H5DataSourceQueryVO dataSourceVo) {
        return h5DataSourceService.page(dataSourceVo);
    }

    @Override
    public void add(H5DataSourceInfo dataSource) {
        h5DataSourceService.add(dataSource);
    }

    @Override
    public H5DataSourceInfo previewData(Long id) {
        return h5DataSourceService.previewData(id);
    }

    @Override
    public void delete(Long id) {
        h5DataSourceService.delete(id);
    }

    @Override
    public void update(H5DataSourceInfo dataSource) {
        h5DataSourceService.update(dataSource);
    }
}
