package com.citc.nce.h5.service;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.h5.vo.H5DataSourceInfo;
import com.citc.nce.h5.vo.req.H5DataSourceQueryVO;
/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xiawenzhu
 * @since 2023-06-15
 */
public interface H5DataSourceService {

    PageResult<H5DataSourceInfo> page(H5DataSourceQueryVO dataSourceVo);

    void add(H5DataSourceInfo dataSource);

    H5DataSourceInfo previewData(Long id);

    void delete(Long id);

    void update(H5DataSourceInfo dataSource);
}
