package com.citc.nce.authcenter.dyzCallBack;

import com.citc.nce.authcenter.dyzCallBack.vo.DyzCollBack;
import com.citc.nce.authcenter.dyzCallBack.vo.DyzR;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.Map;


/**
 * bydud
 * 2024/1/12
 **/
@FeignClient(value = "authcenter-service", contextId = "dyzCallBackApi", url = "${authCenter:}")
public interface DyzCallBackApi {
    @PostMapping("/dyzCallBack/userCheck")
    DyzR<Map<String, Integer>> userCheck(@RequestBody @Valid DyzCollBack dyzCollBack);
}
