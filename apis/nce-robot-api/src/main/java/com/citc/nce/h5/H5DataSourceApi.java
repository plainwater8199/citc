package com.citc.nce.h5;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.h5.vo.H5DataSourceInfo;
import com.citc.nce.h5.vo.req.H5DataSourceQueryVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;




@FeignClient(value = "rebot-service",contextId="H5DataSourceApi", url = "${robot:}")
public interface H5DataSourceApi {


    @ApiOperation("数据源-列表")
    @PostMapping("/data-source/list")
    PageResult<H5DataSourceInfo> page(@RequestBody H5DataSourceQueryVO dataSourceVo) ;


    @ApiOperation("数据源-添加")
    @PostMapping("/data-source/create")
    void add(@RequestBody H5DataSourceInfo dataSource) ;


    @ApiOperation("数据源-详情")
    @GetMapping("/data-source/one")
    H5DataSourceInfo previewData(@RequestParam("id") Long id) ;


    @ApiOperation("数据源-删除")
    @GetMapping("/data-source/delete")
    void delete(@RequestParam("id") Long id);


    @ApiOperation("数据源-更新")
    @PostMapping("/data-source/update")
    void update(@RequestBody H5DataSourceInfo dataSource);

}
