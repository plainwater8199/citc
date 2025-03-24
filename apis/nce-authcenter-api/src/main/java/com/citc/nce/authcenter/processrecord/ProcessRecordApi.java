package com.citc.nce.authcenter.processrecord;


import com.citc.nce.misc.record.req.BusinessIdReq;
import com.citc.nce.misc.record.resp.ProcessingRecordResp;
import io.swagger.annotations.Api;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @author bydud
 * @since 11:25
 */
@Api(tags = "用户中心--站内信模块")
@FeignClient(value = "authcenter-service", contextId = "processRecordApi", url = "${authCenter:}")
public interface ProcessRecordApi {
    @PostMapping("/record/findProcessingRecordList")
    List<ProcessingRecordResp> findProcessingRecordList(@RequestBody @Valid BusinessIdReq req);

}
