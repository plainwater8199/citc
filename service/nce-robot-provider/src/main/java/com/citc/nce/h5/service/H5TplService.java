package com.citc.nce.h5.service;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.h5.vo.H5TplInfo;
import com.citc.nce.h5.vo.req.H5TplQueryVO;

public interface H5TplService {
    void create(H5TplInfo tpl);

    H5TplInfo previewData(Long id);

    PageResult<H5TplInfo> page(H5TplQueryVO h5Vo);

    void update(H5TplInfo h5Info);

    void delete(Long id);
}
