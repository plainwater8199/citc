package com.citc.nce.filter;

import cn.dev33.satoken.exception.NotLoginException;
import cn.hutool.json.JSONObject;
import com.citc.nce.configure.AimCallBackConfigure;
import com.citc.nce.utils.AESUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.*;

@RestControllerAdvice
@Slf4j
@Order(2)
public class AimCallBackTokenFilter implements Filter {

    private final AimCallBackConfigure aimCallBackConfigure;

    private static final String PREFIX = "citc:";
    private static final String DATE_FORMAT = "yyyyMMddHHmmss";

    @Resource
    private AESUtils aesUtils;

    private static final List<String> aimUriList = Arrays.asList("/aim/callBack/queryProjectInfo", "/aim/callBack/smsCallBack");
    private static final List<String> privateNumberUriList = Arrays.asList("/privateNumber/callBack/queryProjectInfo", "/privateNumber/callBack/smsCallBack");

    public AimCallBackTokenFilter(AimCallBackConfigure aimCallBackConfigure) {
        this.aimCallBackConfigure = aimCallBackConfigure;
    }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {

        try {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            String uri = httpServletRequest.getRequestURI();
            if (aimUriList.contains(uri) || privateNumberUriList.contains(uri)) {
                // 验证token
                String cipherText = httpServletRequest.getHeader("aimToken");
                if (StringUtils.isNotEmpty(cipherText)) {//校验token是否传
                    String key = aimUriList.contains(uri) ? aimCallBackConfigure.getTokenKey() : aimCallBackConfigure.getPrivateNumberKey();
                    String secret = aimUriList.contains(uri) ? aimCallBackConfigure.getTokenSecret() : aimCallBackConfigure.getPrivateNumberSecret();
                    String decrypt = aesUtils.decrypt(cipherText, key);
                    //校验token是否正确
                    if(StringUtils.contains(decrypt, PREFIX) && StringUtils.contains(decrypt, secret)) {
                        String callTime = decrypt.substring(decrypt.indexOf(PREFIX) + PREFIX.length(), DATE_FORMAT.length()+ PREFIX.length());
                        //校验时间是否过期
                        if (!isTokenExpired(callTime)) {
                            chain.doFilter(request, response);
                        }else{
                            returnJson(response, 403, "请求时间校验失败");
                        }
                    }else{
                        returnJson(response,403,"toke校验失败");
                    }
                }else{
                    returnJson(response,403,"缺少参数，请验证后重试");
                }
            }else{
                chain.doFilter(request, response);
            }
        } catch (Exception e) {
            log.error("AimCallBackTokenFilter ", e);
            throw NotLoginException.newInstance("PC", "login error");
        }
    }


    private boolean isTokenExpired(String callTime) {
        try {
            Date date = DateUtils.parseDate(callTime, DATE_FORMAT);
            Calendar now = Calendar.getInstance();
            now.setTime(date);

            Calendar before = Calendar.getInstance();
            before.add(Calendar.MINUTE, -5);

            Calendar after = Calendar.getInstance();
            after.add(Calendar.MINUTE, 5);

            return !now.after(before) || !now.before(after);
        } catch (ParseException e) {
            log.info("callTime parse error", e);
            return true;
        }
    }

    private void returnJson(ServletResponse response, int errorCode, String errorMessage) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("code", errorCode);
        jsonObject.set("msg", errorMessage);
        try (PrintWriter writer = response.getWriter()) {
            writer.print(jsonObject);
        } catch (IOException e) {
            log.error("response error", e);
        }
    }
}
