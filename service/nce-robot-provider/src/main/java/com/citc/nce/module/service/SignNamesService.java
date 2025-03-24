package com.citc.nce.module.service;

import com.citc.nce.module.vo.req.SignNamesReq;
import com.citc.nce.common.core.pojo.PageResult;

import java.util.List;
import java.util.Set;

public interface SignNamesService {

    int saveSignNames(SignNamesReq req);

    int updateSignNames(SignNamesReq req);

    PageResult<SignNamesReq> getSignNamesList(SignNamesReq req);

    Long getSignNamesCount(String signModuleId);

    int saveSignNamesForButton(SignNamesReq req);

    Set<String> getSubscribePhones(String moduleId);
}
