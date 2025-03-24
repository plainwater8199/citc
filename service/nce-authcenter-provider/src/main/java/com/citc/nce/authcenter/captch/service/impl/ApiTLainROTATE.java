package com.citc.nce.authcenter.captch.service.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.citc.nce.filecenter.FileApi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

/**
 * 远程维护facade
 *
 * @author bydud
 * @date 2024/11/1
 **/
@Component
@AllArgsConstructor
@Slf4j
public class ApiTLainROTATE {

    private final DiscoveryService discoveryService;
    private final FileApi fileApi;
    private final static String serverName = "authcenter-facade";


    public void addResource(@RequestBody List<String> fileIds) {
        for (ServiceInstance instance : discoveryService.listServices(serverName)) {
            String url = "http://" + instance.getHost() + ":" + instance.getPort() + "/captcha/getImageCaptcha/addResource";
            log.info(url);
            HttpUtil.post(url, JSON.toJSONString(getUrlList(fileIds)));
        }
    }


    public void upgrade(@RequestBody List<String> fileIds) {
        for (ServiceInstance instance : discoveryService.listServices(serverName)) {
            String url = "http://" + instance.getHost() + ":" + instance.getPort() + "/captcha/getImageCaptcha/upgrade";
            log.info(url);
            HttpUtil.post(url, JSON.toJSONString(getUrlList(fileIds)));
        }
    }

    private List<String> getUrlList(List<String> fileIds) {
        //todo 通过文件id查询文件地址

        return new ArrayList<>(fileIds);

    }


}
