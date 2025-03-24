package com.citc.nce.auth.submitdata.controller;

import com.citc.nce.auth.submitdata.SubmitDataApi;
import com.citc.nce.auth.submitdata.service.SubmitDataService;
import com.citc.nce.auth.submitdata.vo.*;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @Author: yangchuang
 * @Date: 2022/8/8 15:57
 * @Version: 1.0
 * @Description: 提交数据
 */
@RestController()
@Slf4j
public class SubmitDataController implements SubmitDataApi {
    @Resource
    private SubmitDataService submitDataService;

    /**
     * 提交数据列表分页获取
     *
     * @param
     * @return
     */
    @ApiOperation(value = "提交数据列表分页获取", notes = "提交数据列表分页获取")
    @PostMapping("/submit/data/pageList")
    @Override
    public PageResultResp getSubmitDatas(@RequestBody @Valid SubmitDataPageReq submitDataPageReq) {
        return submitDataService.getSubmitDatas(submitDataPageReq);
    }

    /**
     * 新增提交数据
     *
     * @param
     * @return
     */
    @ApiOperation(value = "新增提交数据", notes = "新增提交数据")
    @PostMapping("/submit/data/save")
    @Override
    public int saveSubmitData(@RequestBody  @Valid SubmitDataReq submitDataReq) {
        return submitDataService.saveSubmitData(submitDataReq);
    }

       /**
     * 删除提交数据
     *
     * @param
     * @return
     */
    @ApiOperation(value = "删除提交数据", notes = "删除提交数据")
    @PostMapping("/submit/data/delete")
    @Override
    public int delSubmitDataById(@RequestBody  @Valid SubmitDataOneReq submitDataOneReq) {
        return submitDataService.delSubmitDataById(submitDataOneReq);
    }

    /**
     * 根据id获取提交数据
     *
     * @param
     * @return
     */
    @ApiOperation(value = "根据id获取提交数据", notes = "根据id获取提交数据")
    @PostMapping("/submit/data/getOne")
    @Override
    public SubmitDataResp getSubmitDataById(@RequestBody  @Valid SubmitDataOneReq submitDataOneReq) {
        return submitDataService.getSubmitDataById(submitDataOneReq);
    }

    /**
     * 提交数据列表导出数据分页获取
     *
     * @param
     * @return
     */
    @ApiOperation(value = "提交数据列表导出数据分页获取", notes = "提交数据列表导出数据分页获取")
    @PostMapping("/submit/data/downloadPageList")
    @Override
    public PageResultResp downloadPageList(@RequestBody @Valid SubmitDataPageReq submitDataPageReq) {
        return submitDataService.downloadPageList(submitDataPageReq);
    }

    @PostMapping("/submit/data/{id}")
    @Override
    public SubmitDataResp getSubmitDataById(@PathVariable("id") Long id) {
        if (null != id) {
            SubmitDataOneReq submitDataOneReq = new SubmitDataOneReq();
            submitDataOneReq.setId(id);
            submitDataService.getSubmitDataById(submitDataOneReq);
        }
        return null;
    }

    @Override
    public String getShortUrl(SubmitDataOneReq submitDataOneReq) {
        return submitDataService.getShortUrl(submitDataOneReq.getId());
    }

}
