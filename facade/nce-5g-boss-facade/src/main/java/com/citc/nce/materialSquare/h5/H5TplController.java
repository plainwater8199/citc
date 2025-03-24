package com.citc.nce.materialSquare.h5;


import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.h5.H5TplApi;
import com.citc.nce.h5.vo.H5TplInfo;
import com.citc.nce.h5.vo.req.H5TplQueryVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tpl")
@Api(tags = "H5-模版管理")
public class H5TplController {

    @Autowired
    private H5TplApi h5TplApi;

    /**
     * h5-模版新增
     * @param tpl 模版内容
     */
    @ApiOperation("h5-模版新增")
    @PostMapping("/create")
    public void create(@RequestBody H5TplInfo tpl){
        h5TplApi.create(tpl);
    }


    /**
     * 通过ID查询
     *
     * @param id ID
     * @return H5
     */
    @ApiOperation("h5-模版查询")
    @GetMapping("/one")
    public H5TplInfo previewData(@RequestParam("id") Long id){
        return h5TplApi.previewData(id);
    }

    /**
     * h5-模版列表
     */
    @ApiOperation("h5-模版列表")
    @PostMapping("/list")
    public PageResult<H5TplInfo> page(@RequestBody H5TplQueryVO h5Vo){
        return h5TplApi.page(h5Vo);
    }


    /**
     * h5-更新
     */
    @ApiOperation("h5-模版更新")
    @PostMapping("/update")
    public void update(@RequestBody H5TplInfo h5Info){
        h5TplApi.update(h5Info);
    }
    /**
     * h5-删除
     */
    @ApiOperation("h5-模版删除")
    @GetMapping("/delete")
    public void delete(@RequestParam("id") Long id){
        h5TplApi.delete(id);
    }

}
