package com.citc.nce.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 通过FeignClient工厂获取到的FeignClient对象通过指定的请求去调用生产者方法!
 */
@Component
public class DynamicClient {
    @Autowired
    private DynamicFeignClientFactory<DynamicService> dynamicDynamicFeignClientFactory;

    public Object executePostApi(String feignName,String url,Object params){
        DynamicService dynamicService = dynamicDynamicFeignClientFactory.getFeignClient(DynamicService.class,feignName);
        return dynamicService.executePostRequest(url,params);
    }
    public Object executeGetApi(String feignName,String url,Object params){
        DynamicService dynamicService = dynamicDynamicFeignClientFactory.getFeignClient(DynamicService.class,feignName);
        return dynamicService.executeGetRequest(url, params);
    }
}
