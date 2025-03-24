package com.citc.nce.datadictionary;

import com.citc.nce.misc.dictionary.DataDictionaryApi;
import com.citc.nce.misc.dictionary.vo.req.BaseAreaReq;
import com.citc.nce.misc.dictionary.vo.req.DataDictionaryListReq;
import com.citc.nce.misc.dictionary.vo.req.DictionaryReq;
import com.citc.nce.misc.dictionary.vo.resp.BaseAreaResp;
import com.citc.nce.misc.dictionary.vo.resp.DictionaryResp;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/8/11 14:37
 * @Version: 1.0
 * @Description:
 */
@RestController
@RequestMapping("/misc/dic")
@Slf4j
@Api(value = "misc", tags = "字典管理")
public class DataDictionaryController {

    @Resource
    private DataDictionaryApi dataDictionaryApi;

    @PostMapping("/getDictionaryByType")
    public List<DictionaryResp> getDictionaryByType(@RequestBody @Valid DictionaryReq req) {
        return dataDictionaryApi.getDictionaryByType(req);
    }

    @PostMapping("/importDataDictionary")
    public boolean importDataDictionary(@RequestBody @Valid DataDictionaryListReq req) {
        return dataDictionaryApi.importDataDictionary(req);
    }

    @GetMapping({"/region"})
    public List<BaseAreaResp> findRegion(){
        return dataDictionaryApi.findRegion();
    }

    @GetMapping({"/industry"})
    public List<BaseAreaResp> findIndustry(){
        return dataDictionaryApi.findIndustry();
    }

    @PostMapping({"/findByParentId"})
    public List<BaseAreaResp> findByParentId(@RequestBody BaseAreaReq req){
        return dataDictionaryApi.findByParentId(req);
    }
}
