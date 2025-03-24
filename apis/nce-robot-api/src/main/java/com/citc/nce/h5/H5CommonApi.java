package com.citc.nce.h5;


import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.h5.vo.H5Info;
import com.citc.nce.h5.vo.req.H5QueryVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "rebot-service",contextId="H5CommonApi", url = "${robot:}")
public interface H5CommonApi {



    /**
     * H5-公共管理-获取省市级联
     *
     * @return H5
     */
    @ApiOperation("H5-公共管理-获取省市级联")
    @GetMapping("/common/city")
    Object city() ;

//    /**
//     * H5-公共管理-后台预览页面
//     *
//     * @param id ID
//     * @return H5
//     */
//    @ApiOperation("H5-公共管理-后台预览页面")
//    @GetMapping("/preview")
//    H5Info preview(@RequestParam("id") Long id) ;
//
//
//    /**
//     * H5-公共管理-获取精选模版列表
//     */
//    @ApiOperation("H5-公共管理-获取精选模版列表")
//    @RequestMapping("/publicTpl/list")
//    PageResult<H5Info> publicTplList(@RequestBody H5QueryVO h5Vo);
//
//    /**
//     * H5-公共管理-获取精选模版详情
//     */
//    @ApiOperation("H5-公共管理-获取精选模版详情")
//    @PostMapping("/publicTpl/one")
//    void publicTplOne(@RequestParam("id") Long id) ;



}
