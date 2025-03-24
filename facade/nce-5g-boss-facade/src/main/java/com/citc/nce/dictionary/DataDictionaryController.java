package com.citc.nce.dictionary;

import com.citc.nce.annotation.BossAuth;
import com.citc.nce.misc.dictionary.DataDictionaryApi;
import com.citc.nce.misc.dictionary.vo.req.DataDictionaryListReq;
import com.citc.nce.misc.dictionary.vo.req.DeleteReq;
import com.citc.nce.misc.dictionary.vo.req.DictionaryReq;
import com.citc.nce.misc.dictionary.vo.resp.DictionaryResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @Author: wenliuch
 * @Date: 2022/6/28 16:04
 * @Version: 1.0
 * @Description:
 */
@Api(tags = "后台管理-字典表服务")
@RestController
@BossAuth("all")
public class DataDictionaryController {

    @Resource
    private DataDictionaryApi dataDictionaryApi;

    /**
     * 根据字典类型获取字典项
     *
     * @param req
     * @return
     */
    @ApiOperation("根据字典类型获取字典项")
    @PostMapping("/misc/dic/getDictionaryByType")
    List<DictionaryResp> getDictionaryByType(@RequestBody DictionaryReq req) {
        return dataDictionaryApi.getDictionaryByType(req);
    }

    /**
     * 导入数据到字典表
     *
     * @param req
     * @return
     */
    @ApiOperation("导入数据到字典表")
    @PostMapping("/misc/dic/importDataDictionary")
    boolean importDataDictionary(@RequestBody DataDictionaryListReq req) {
        return dataDictionaryApi.importDataDictionary(req);
    }

    /**
     * 修改API分类字典
     */
    @ApiOperation("修改API分类字典")
    @PostMapping("/misc/dic/updateApiTypeDictionary")
    public void updateApiTypeDictionary(@RequestBody @Valid DeleteReq req) {
        dataDictionaryApi.updateApiTypeDictionary(req);
    }
}
