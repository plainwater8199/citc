package com.citc.nce.auth.submitdata;

import com.citc.nce.auth.submitdata.vo.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/10/13 16:53
 * @Version: 1.0
 * @Description:提交数据
 */

@FeignClient(value = "auth-service", contextId = "SubmitDataApi", url = "${auth:}")
public interface SubmitDataApi {
    /**
     * 提交数据列表分页获取
     *
     * @param
     * @return
     */
    @PostMapping("/submit/data/pageList")
    PageResultResp getSubmitDatas(@RequestBody @Valid SubmitDataPageReq submitDataPageReq);

    /**
     * 新增提交数据
     *
     * @param
     * @return
     */
    @PostMapping("/submit/data/save")
    int saveSubmitData(@RequestBody @Valid SubmitDataReq submitDataReq);

       /**
     * 删除提交数据
     *
     * @param
     * @return
     */
    @PostMapping("/submit/data/delete")
    int delSubmitDataById(@RequestBody @Valid SubmitDataOneReq submitDataOneReq);

    /**
     * 获取单个提交数据
     *
     * @param
     * @return
     */
    @PostMapping("/submit/data/getOne")
    SubmitDataResp getSubmitDataById(@RequestBody @Valid SubmitDataOneReq submitDataOneReq);

    /**
     * 提交数据列表导出数据分页获取
     *
     * @param
     * @return
     */
    @PostMapping("/submit/data/downloadPageList")
    PageResultResp downloadPageList(@RequestBody @Valid SubmitDataPageReq submitDataPageReq);

    @PostMapping("/submit/data/{id}")
    SubmitDataResp getSubmitDataById(@PathVariable("id") Long id);

    @PostMapping("/submit/data/getShortUrl")
    String getShortUrl(@RequestBody @Valid SubmitDataOneReq submitDataOneReq);
}
