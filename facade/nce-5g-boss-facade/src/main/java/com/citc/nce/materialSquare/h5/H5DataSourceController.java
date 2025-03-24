package com.citc.nce.materialSquare.h5;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.h5.H5DataSourceApi;
import com.citc.nce.h5.vo.H5DataSourceInfo;
import com.citc.nce.h5.vo.req.H5DataSourceQueryVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@Api(tags = "H5-数据源管理")
public class H5DataSourceController {

    @Autowired
    private H5DataSourceApi h5DataSourceApi;


    @ApiOperation("数据源-列表")
    @PostMapping("/data-source/list")
    public PageResult<H5DataSourceInfo> page(@RequestBody H5DataSourceQueryVO dataSourceVo) {
        return h5DataSourceApi.page(dataSourceVo);
    }

    @ApiOperation("数据源-添加")
    @PostMapping("/data-source/create")
    public void add(@RequestBody H5DataSourceInfo dataSource) {
        h5DataSourceApi.add(dataSource);
    }

    @ApiOperation("数据源-详情")
    @GetMapping("/data-source/one")
    public H5DataSourceInfo previewData(@RequestParam("id") Long id) {
        return h5DataSourceApi.previewData(id);
    }

    @ApiOperation("数据源-删除")
    @GetMapping("/data-source/delete")
    public void delete(@RequestParam("id") Long id) {
        h5DataSourceApi.delete(id);
    }

    @ApiOperation("数据源-更新")
    @PostMapping("/data-source/update")
    public void update(@RequestBody H5DataSourceInfo dataSource) {
        h5DataSourceApi.update(dataSource);
    }
}
