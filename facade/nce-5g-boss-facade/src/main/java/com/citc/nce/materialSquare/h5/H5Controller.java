package com.citc.nce.materialSquare.h5;


import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.facadeserver.annotations.SkipToken;
import com.citc.nce.h5.H5Api;
import com.citc.nce.h5.vo.H5Info;
import com.citc.nce.h5.vo.req.H5FromSubmitReq;
import com.citc.nce.h5.vo.req.H5QueryVO;
import com.citc.nce.h5.vo.resp.H5Resp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/h5")
@Api(tags = "H5-H5模版管理")
public class H5Controller{

    @Autowired
    private H5Api h5Api;


    /**
     * 通过ID查询
     *
     * @param id ID
     * @return H5
     */
    @ApiOperation("h5-查询")
    @GetMapping("/one")
    public H5Info previewData(@RequestParam("id") Long id) {
        return h5Api.previewData(id);
    }

    /**
     * h5 详情
     *
     * @param id ID
     * @return H5
     */
    @SkipToken
    @ApiOperation("h5-详情")
    @GetMapping("/detail")
    public H5Info getDetail(@RequestParam("id") Long id) {
        return h5Api.getDetail(id);
    }


    /**
     * h5-列表
     */
    @ApiOperation("h5-列表")
    @PostMapping("/list")
    public PageResult<H5Info> page(@RequestBody H5QueryVO h5Vo) {
        return h5Api.page(h5Vo);
    }

    @ApiOperation("h5-新增")
    @PostMapping("/create")
    public H5Resp create(@RequestBody H5Info h5) {
        return h5Api.create(h5);
    }

    /**
     * h5-更新
     */
    @ApiOperation("h5-更新")
    @PostMapping("/update")
    public void update(@RequestBody H5Info h5Info) {
        h5Api.update(h5Info);
    }

    /**
     * h5-删除
     */
    @ApiOperation("h5-删除")
    @GetMapping("/delete")
    public void delete(@RequestParam("id") Long id) {
        h5Api.delete(id);
    }



    /**
     * h5-复制
     */
    @ApiOperation("h5-复制")
    @PostMapping("/copy")
    public void copy(@RequestBody H5Info h5Temp) {
        h5Api.copy(h5Temp);
    }


    /**
     * h5-表单提交
     */
    @ApiOperation("h5-表单提交")
    @PostMapping("/form/submit")
    @SkipToken
    public void formSubmit(@RequestBody H5FromSubmitReq req) {
        h5Api.formSubmit(req);
    }



}
