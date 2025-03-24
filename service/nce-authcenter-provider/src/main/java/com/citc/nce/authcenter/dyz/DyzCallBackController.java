package com.citc.nce.authcenter.dyz;

import com.citc.nce.authcenter.dyz.impl.DyzService;
import com.citc.nce.authcenter.dyzCallBack.DyzCallBackApi;
import com.citc.nce.authcenter.dyzCallBack.vo.DyzCollBack;
import com.citc.nce.authcenter.dyzCallBack.vo.DyzR;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

/**
 * 对因子短信调用
 * bydud
 * 2024/1/12
 **/
@RestController
public class DyzCallBackController implements DyzCallBackApi {

    @Resource
    private DyzService dyzService;

    @Override
    public DyzR<Map<String, Integer>> userCheck(@RequestBody @Valid DyzCollBack dyzCollBack) {
        return Integer.valueOf(0).equals(dyzService.userCheck(dyzCollBack)) ? DyzR.pass() : DyzR.fail();
    }
}
