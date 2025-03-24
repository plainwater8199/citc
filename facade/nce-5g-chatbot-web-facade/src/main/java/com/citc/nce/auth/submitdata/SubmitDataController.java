package com.citc.nce.auth.submitdata;

import com.citc.nce.auth.submitdata.vo.*;
import com.citc.nce.common.facadeserver.annotations.SkipToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @Author: yangchuang
 * @Date: 2022/8/8 15:57
 * @Version: 1.0
 * @Description: 提交数据
 */
@RestController
@Slf4j
@Api(value = "auth", tags = "提交数据")
public class SubmitDataController {
    @Resource
    private SubmitDataApi submitDataApi;

    /**
     * 提交数据列表分页获取
     *
     * @param
     * @return
     */
    @SkipToken
    @ApiOperation(value = "提交数据列表分页获取", notes = "提交数据列表分页获取")
    @PostMapping("/submit/data/pageList")
    public PageResultResp getSubmitDatas(@RequestBody @Valid SubmitDataPageReq submitDataPageReq) {
        return submitDataApi.getSubmitDatas(submitDataPageReq);
    }

    /**
     * 新增提交数据
     *
     * @param
     * @return
     */
    @SkipToken
    @ApiOperation(value = "新增提交数据", notes = "新增提交数据")
    @PostMapping("/submit/data/save")
    public int saveSubmitData(@RequestBody  @Valid SubmitDataReq submitDataReq) {
        return submitDataApi.saveSubmitData(submitDataReq);
    }

       /**
     * 删除提交数据
     *
     * @param
     * @return
     */
    @SkipToken
    @ApiOperation(value = "删除提交数据", notes = "删除提交数据")
    @PostMapping("/submit/data/delete")
    public int delSubmitDataById(@RequestBody  @Valid SubmitDataOneReq submitDataOneReq) {
        return submitDataApi.delSubmitDataById(submitDataOneReq);
    }

    /**
     * 根据id获取提交数据
     *
     * @param
     * @return
     */
    @SkipToken
    @ApiOperation(value = "根据id获取提交数据", notes = "根据id获取提交数据")
    @PostMapping("/submit/data/getOne")
    public SubmitDataResp getSubmitDataById(@RequestBody  @Valid SubmitDataOneReq submitDataOneReq) {
        return submitDataApi.getSubmitDataById(submitDataOneReq);
    }

    /**
     * 提交数据列表导出数据分页获取
     *
     * @param
     * @return
     */
    @SkipToken
    @ApiOperation(value = "提交数据列表导出数据分页获取", notes = "提交数据列表导出数据分页获取")
    @PostMapping("/submit/data/downloadPageList")
    public PageResultResp downloadPageList(@RequestBody @Valid SubmitDataPageReq submitDataPageReq) {
        return submitDataApi.downloadPageList(submitDataPageReq);
    }
    @SkipToken
    @ApiOperation(value = "提交数据列表导出数据分页获取", notes = "提交数据列表导出数据分页获取")
    @PostMapping("/submit/data/getShortUrl")
    public String getShortUrl(@RequestBody @Valid SubmitDataOneReq submitDataOneReq) {
        return submitDataApi.getShortUrl(submitDataOneReq);
    }

}
