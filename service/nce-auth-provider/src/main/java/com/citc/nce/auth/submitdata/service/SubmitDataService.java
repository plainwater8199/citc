package com.citc.nce.auth.submitdata.service;

import com.citc.nce.auth.submitdata.vo.*;
import com.citc.nce.auth.submitdata.vo.PageResultResp;

import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/8/15 16:30
 * @Version: 1.0
 * @Description:
 */
public interface SubmitDataService {

    PageResultResp getSubmitDatas(SubmitDataPageReq submitDataPageReq);

    int saveSubmitData(SubmitDataReq submitDataReq);

    int delSubmitDataById(SubmitDataOneReq submitDataOneReq);

    SubmitDataResp getSubmitDataById(SubmitDataOneReq submitDataOneReq);

    PageResultResp downloadPageList(SubmitDataPageReq submitDataPageReq);

    String getShortUrl(Long id);
}
