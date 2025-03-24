package com.citc.nce.rebot.manage;

import com.citc.nce.common.facadeserver.annotations.SkipToken;
import com.citc.nce.common.facadeserver.annotations.UnWrapResponse;
import com.citc.nce.robot.api.SmsPlatformApi;
import com.citc.nce.robot.sms.ReportResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author jcrenc
 * @since 2024/3/18 16:01
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@UnWrapResponse
public class SmsNotifyController {
    private final SmsPlatformApi smsPlatformApi;
    private final ObjectMapper objectMapper;

    @SkipToken
    @PostMapping({"/sms/messageNotify"})
    @ApiOperation("短信发送结果状态回调")
    public String handleSendResultCallback(@RequestParam("reports") String reportStr) throws JsonProcessingException {
        List<ReportResponse> reports = objectMapper.readValue(reportStr, new TypeRef());
        log.info("短信发送结果状态回调:{}", reports);
        smsPlatformApi.handleSendResultCallback(reports);
        return "SUCCESS";
    }

    public static class TypeRef extends TypeReference<List<ReportResponse>> {
    }

}
