package com.citc.nce.h5;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/common")
@Api(tags = "H5-公共管理")
public class H5CommonController {

    @Autowired
    private H5CommonApi h5CommonApi;


    /**
     * H5-公共管理-获取省市级联
     *
     * @return H5
     */
    @ApiOperation("H5-公共管理-获取省市级联")
    @GetMapping("/city")
    public Object city() {
        return h5CommonApi.city();
    }

//    /**
//     * H5-公共管理-后台预览页面
//     *
//     * @param id ID
//     * @return H5
//     */
//    @ApiOperation("H5-公共管理-后台预览页面")
//    @GetMapping("/preview")
//    public H5Info preview(@RequestParam("id") Long id) {
//        return h5CommonApi.preview(id);
//    }
//
//
//    /**
//     * H5-公共管理-获取精选模版列表
//     */
//    @ApiOperation("H5-公共管理-获取精选模版列表")
//    @RequestMapping("/publicTpl/list")
//    public PageResult<H5Info> publicTplList(@RequestBody H5QueryVO h5Vo) {
//        return h5CommonApi.publicTplList(h5Vo);
//    }
//
//
//    /**
//     * H5-公共管理-获取精选模版详情
//     */
//    @ApiOperation("H5-公共管理-获取精选模版详情")
//    @PostMapping("/publicTpl/one")
//    public void publicTplOne(@RequestParam("id") Long id) {
//        h5CommonApi.publicTplOne(id);
//    }

}
