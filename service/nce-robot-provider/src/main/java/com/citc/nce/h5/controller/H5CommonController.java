package com.citc.nce.h5.controller;


import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.h5.H5Api;
import com.citc.nce.h5.H5CommonApi;
import com.citc.nce.h5.service.H5CommonService;
import com.citc.nce.h5.service.H5Service;
import com.citc.nce.h5.vo.H5Info;
import com.citc.nce.h5.vo.req.H5FromSubmitReq;
import com.citc.nce.h5.vo.req.H5QueryVO;
import com.citc.nce.h5.vo.resp.H5CopyResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class H5CommonController implements H5CommonApi {

    @Autowired
    private H5CommonService h5CommonService;


    @Override
    public Object city() {
        return h5CommonService.getCityList();
    }
}
