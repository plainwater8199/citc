package com.citc.nce.dyz;

import com.alibaba.fastjson.JSON;
import com.citc.nce.authcenter.dyzCallBack.DyzCallBackApi;
import com.citc.nce.authcenter.dyzCallBack.vo.DyzCollBack;
import com.citc.nce.authcenter.dyzCallBack.vo.DyzR;
import com.citc.nce.common.facadeserver.annotations.SkipToken;
import com.citc.nce.common.facadeserver.annotations.UnWrapResponse;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author jiancheng
 */
@Api(tags = "dyz")
@RestController
@RequiredArgsConstructor
@Slf4j
public class DyzController {

    private final DyzCallBackApi dyzCallBackApi;

    /**
     * dyz平台确认用户身份
     *
     * @param dyzCollBack 用户和鉴权数据
     * @return 0 身份合法 1 不合法
     */
    @PostMapping("/dyzCallBack/userCheck")
    @UnWrapResponse
    @SkipToken
    public DyzR<Map<String, Integer>> userCheck(@RequestBody DyzCollBack dyzCollBack) {
        try {
            log.info("dyz平台确认用户身份 {}", JSON.toJSONString(dyzCollBack));
            return dyzCallBackApi.userCheck(dyzCollBack);
        } catch (Exception e) {
            log.error("dyz平台确认用户身份 失败", e);
        }
        return DyzR.fail();
    }

}
