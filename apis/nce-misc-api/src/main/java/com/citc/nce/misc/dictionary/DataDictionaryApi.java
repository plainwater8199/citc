package com.citc.nce.misc.dictionary;

import com.citc.nce.misc.dictionary.vo.req.BaseAreaReq;
import com.citc.nce.misc.dictionary.vo.req.DataDictionaryListReq;
import com.citc.nce.misc.dictionary.vo.req.DeleteReq;
import com.citc.nce.misc.dictionary.vo.req.DictionaryReq;
import com.citc.nce.misc.dictionary.vo.resp.BaseAreaResp;
import com.citc.nce.misc.dictionary.vo.resp.DictionaryResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022/6/28 16:04
 * @Version: 1.0
 * @Description:
 */
@FeignClient(value = "misc-service", contextId = "DataDictionaryApi", url = "${miscServer:}")
public interface DataDictionaryApi {
    /**
     * 根据字典类型获取字典项
     *
     * @param req
     * @return
     */
    @PostMapping("/misc/dic/getDictionaryByType")
    List<DictionaryResp> getDictionaryByType(@RequestBody @Valid DictionaryReq req);

    /**
     * 导入数据到字典表
     *
     * @param req
     * @return
     */
    @PostMapping("/misc/dic/importDataDictionary")
    boolean importDataDictionary(@RequestBody @Valid DataDictionaryListReq req);

    /**
     * 根据字典类型获取字典项信息
     *
     * @param req
     * @return
     */
    @PostMapping("/misc/dic/getDictionaryInfoByType")
    Map<String, DictionaryResp> getDictionaryInfoByType(@RequestBody @Valid DictionaryReq req);

    @PostMapping("/misc/dic/updateApiTypeDictionary")
    void updateApiTypeDictionary(@RequestBody @Valid DeleteReq req);

    @PostMapping("/misc/dic/region")
    List<BaseAreaResp> findRegion();

    @PostMapping("/misc/dic/industry")
    List<BaseAreaResp> findIndustry();

    @PostMapping("/misc/dic/findByParentId")
    List<BaseAreaResp> findByParentId(@RequestBody BaseAreaReq areaReq);
}
