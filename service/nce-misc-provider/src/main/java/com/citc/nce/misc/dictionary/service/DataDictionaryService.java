package com.citc.nce.misc.dictionary.service;

import com.citc.nce.misc.dictionary.vo.req.DataDictionaryListReq;
import com.citc.nce.misc.dictionary.vo.req.DeleteReq;
import com.citc.nce.misc.dictionary.vo.req.DictionaryReq;
import com.citc.nce.misc.dictionary.vo.resp.DictionaryResp;

import java.util.List;
import java.util.Map;


/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022/6/28 16:04
 * @Version: 1.0
 * @Description:
 */
public interface DataDictionaryService {

    List<DictionaryResp> getDictionaryByType(DictionaryReq req);

    boolean importDataDictionary(DataDictionaryListReq req);

    Map<String, DictionaryResp> getDictionaryInfoByType(DictionaryReq req);

    void updateApiTypeDictionary(DeleteReq req);
}

