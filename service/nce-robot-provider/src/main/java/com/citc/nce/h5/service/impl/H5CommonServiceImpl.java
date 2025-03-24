package com.citc.nce.h5.service.impl;

import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.json.JSONUtil;
import com.citc.nce.h5.service.H5CommonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Transactional(rollbackFor = Exception.class)
@Service
public class H5CommonServiceImpl implements H5CommonService {

    @Override
    public Object getCityList() {
        ClassPathResource classPathResource = new ClassPathResource("city.json");
        String cityJson = classPathResource.readUtf8Str();
        return JSONUtil.parseArray(cityJson);
    }
}
