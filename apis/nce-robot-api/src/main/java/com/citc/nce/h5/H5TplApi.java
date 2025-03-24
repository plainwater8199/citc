package com.citc.nce.h5;


import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.h5.vo.H5TplInfo;
import com.citc.nce.h5.vo.req.H5TplQueryVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "rebot-service",contextId="H5TplApi", url = "${robot:}")
public interface H5TplApi {


    /**
     * h5-模版新增
     * @param tpl 模版内容
     */
    @ApiOperation("h5-模版新增")
    @PostMapping("/create")
    void create(@RequestBody H5TplInfo tpl);


    /**
     * 通过ID查询
     *
     * @param id ID
     * @return H5
     */
    @ApiOperation("h5-模版查询")
    @GetMapping("/one")
    H5TplInfo previewData(@RequestParam("id") Long id);

    /**
     * h5-模版列表
     */
    @ApiOperation("h5-模版列表")
    @RequestMapping("/list")
    PageResult<H5TplInfo> page(@RequestBody H5TplQueryVO h5Vo);


    /**
     * h5-更新
     */
    @ApiOperation("h5-模版更新")
    @PostMapping("/update")
    void update(@RequestBody H5TplInfo h5Info);
    /**
     * h5-删除
     */
    @ApiOperation("h5-模版删除")
    @GetMapping("/delete")
    void delete(@RequestParam("id") Long id);




}
