package com.citc.nce.module;

import com.citc.nce.module.vo.req.SignNamesReq;
import com.citc.nce.common.core.pojo.PageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(value = "rebot-service", contextId = "SignNamesApi", url = "${robot:}")
public interface SignNamesApi {

    @PostMapping("/csp/signNames/save")
    int saveSignNames(@RequestBody @Valid SignNamesReq req);

    @PostMapping("/csp/signNames/updateSignNames")
    int updateSignNames(@RequestBody SignNamesReq req);

    @PostMapping("/csp/signNames/getSignNamesList")
    PageResult<SignNamesReq> getSignNamesList(@RequestBody SignNamesReq req);

    @PostMapping("/csp/signNames/saveSignNamesForButton")
    void saveSignNamesForButton(@RequestBody SignNamesReq req);
}
