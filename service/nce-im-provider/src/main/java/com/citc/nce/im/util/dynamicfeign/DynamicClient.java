package com.citc.nce.im.util.dynamicfeign;

import com.citc.nce.im.nacos.NacosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 通过FeignClient工厂获取到的FeignClient对象通过指定的请求去调用生产者方法!
 */
@Component
public class DynamicClient {
    @Autowired
    private NacosService nacosService;
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

    public boolean checkDockerService(String feignName) {
        return nacosService.checkDockerService(feignName);
    }
}
