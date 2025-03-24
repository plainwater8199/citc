package com.citc.nce.misc.dictionary;

import com.citc.nce.misc.dictionary.service.DataDictionaryService;
import com.citc.nce.misc.dictionary.service.IBaseAreaService;
import com.citc.nce.misc.dictionary.vo.req.BaseAreaReq;
import com.citc.nce.misc.dictionary.vo.req.DataDictionaryListReq;
import com.citc.nce.misc.dictionary.vo.req.DeleteReq;
import com.citc.nce.misc.dictionary.vo.req.DictionaryReq;
import com.citc.nce.misc.dictionary.vo.resp.BaseAreaResp;
import com.citc.nce.misc.dictionary.vo.resp.DictionaryResp;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022/6/28 16:04
 * @Version: 1.0
 * @Description:
 */
@RestController
public class DataDictionaryController implements DataDictionaryApi {

    @Resource
    private DataDictionaryService dictionaryService;

    @Resource
    private IBaseAreaService areaService;

    @Override
    @PostMapping("/misc/dic/getDictionaryByType")
    public List<DictionaryResp> getDictionaryByType(@RequestBody @Valid DictionaryReq req) {
        return dictionaryService.getDictionaryByType(req);
    }

    @Override
    @PostMapping("/misc/dic/importDataDictionary")
    public boolean importDataDictionary(@RequestBody @Valid DataDictionaryListReq req) {
        return dictionaryService.importDataDictionary(req);
    }

    /**
     * 根据字典类型获取字典项信息
     *
     * @param req
     * @return
     */
    @Override
    public Map<String, DictionaryResp> getDictionaryInfoByType(@RequestBody @Valid DictionaryReq req) {
        return dictionaryService.getDictionaryInfoByType(req);
    }

    @Override
    @PostMapping("/misc/dic/updateApiTypeDictionary")
    public void updateApiTypeDictionary(@RequestBody @Valid DeleteReq req){
        dictionaryService.updateApiTypeDictionary(req);
    }


    /***
     * 大区查询
     * */
    @PostMapping("/misc/dic/region")
    @Override
    public List<BaseAreaResp> findRegion() {
        return areaService.findRegion();
    }


    /**
     * 行业查询
     * */
    @PostMapping("/misc/dic/industry")
    @Override
    public List<BaseAreaResp> findIndustry() {
        return areaService.findIndustry();
    }

    /**
     * 通过父id查询
     * */
    @PostMapping("/misc/dic/findByParentId")
    @Override
    public List<BaseAreaResp> findByParentId(@RequestBody BaseAreaReq areaReq) {
        return areaService.findByParentId(areaReq);
    }
}
