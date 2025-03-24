package com.citc.nce.filter;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.citc.nce.configure.BaiduSensitiveCheckConfigure;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(BaiduSensitiveCheckConfigure.class)
public class AllFilter implements Filter {

    @Resource
    BaiduService baiduService;

    private final BaiduSensitiveCheckConfigure baiduSensitiveCheckConfigure;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest)servletRequest;
        RequestWrapper requestWrapper = new RequestWrapper(httpServletRequest);
        //审核开关
        if (!baiduSensitiveCheckConfigure.getIsExamine()){
            filterChain.doFilter(requestWrapper,servletResponse);
            return;
        }
        String bodyString = requestWrapper.getBodyString();
        if (!(StringUtils.startsWith(bodyString,"[") && StringUtils.endsWith(bodyString,"]"))){
            JSONObject jsonObject = JSONObject.parseObject(bodyString);
            if (ObjectUtil.isNotEmpty(jsonObject.getInteger("verifySensitive")) && jsonObject.getInteger("verifySensitive") == 0){
                //画布不调用审核
                filterChain.doFilter(requestWrapper,servletResponse);
                return;
            }
        }
        if (bodyString.length() > 6666) {
            log.info("请求长度超长，进行分段审核，字段长度 {}",bodyString.length());
            int count = 1;
            if (ObjectUtil.isNotEmpty(bodyString)) {
                count = (int) Math.ceil(bodyString.length() / 6666.0);
            }
            log.info("分段次数 count {}",count);
            if (count > 1){
                for (int i = 0; i < count; i++) {
                    String text = requestWrapper.getBodyString();
                    if (text.length() > 6666){
                        if (i == (count - 1)) {
                            text = text.substring(i * 6666);
                        } else {
                            text = text.substring(i * 6666, i * 6666 + 6666);
                        }
                    }
                    log.info("第" + (i + 1) + "次审核长度 {}",text.length());
                    JSONObject result = baiduService.textCensorUrl(text);
                    if (result.getInteger("conclusionType") !=  1){
                        servletResponse.setCharacterEncoding("UTF8");
                        List<String> results = geResult(result);
                        servletResponse.getWriter().println(JSONUtil.toJsonStr(new Result(81001152,results)));
                        return;
                    }
                }
                filterChain.doFilter(requestWrapper,servletResponse);
            }
        } else {
            examine(servletResponse, filterChain, requestWrapper, bodyString);
        }

    }

    private List<String> geResult(JSONObject result) {
        JSONArray data = result.getJSONArray("data");
        if (CollectionUtil.isNotEmpty(data)){
            List<String> results = new ArrayList<>();
            for (int j = 0; j < data.size(); j++) {
                String msg = data.getJSONObject(j).getString("msg");
                JSONArray hits = data.getJSONObject(j).getJSONArray("hits");
                JSONArray words = new JSONArray();
                if (CollectionUtil.isNotEmpty(hits)){
                    for (int k = 0; k < hits.size(); k++) {
                        words.addAll(hits.getJSONObject(k).getJSONArray("words"));
                    }
                }
                results.add(msg + " " +(words.toString().replace("[","【")).replace("]","】").replaceAll("\"",""));
            }
            return results;
        }
        return new ArrayList<>();
    }

    private void examine(ServletResponse servletResponse, FilterChain filterChain, RequestWrapper requestWrapper, String bodyString) throws IOException, ServletException {
        JSONObject jsonObject = baiduService.textCensorUrl(bodyString);
        if (jsonObject.getInteger("conclusionType") == 1){
            filterChain.doFilter(requestWrapper,servletResponse);
        }else {
            servletResponse.setCharacterEncoding("UTF8");
            List<String> result = geResult(jsonObject);
            if (CollectionUtil.isEmpty(result)){
                result.add("请求失败，包含敏感信息或不符合相关规定");
            }
            servletResponse.getWriter().println(JSONUtil.toJsonStr(new Result(81001152,result)));
        }
    }

    @Override
    public void destroy() {

    }
}
