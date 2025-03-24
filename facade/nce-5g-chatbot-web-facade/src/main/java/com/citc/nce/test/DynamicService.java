package com.citc.nce.test;


import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 通用接口，里面定义通用方法
 * 注意：由于服务生产者所有的接口的返回值都是json格式的字符串，所以这里的通用接口的返回值最好使用String类型
 */
public interface DynamicService {

    /**
     * http对应的相关方法
     * @param url 服务生成者handler方法请求映射地址
     * @param params 服务生产者handler方法参数
     * @return 返回
     */
    @PostMapping(value = "{url}")
    String executePostRequest(@PathVariable("url") String url, @RequestBody Object params);
    @GetMapping(value = "{url}")
    String executeGetRequest(@PathVariable("url") String url, @SpringQueryMap Object params);
}
