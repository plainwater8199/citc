package com.citc.nce.im.client;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.core.pojo.RestResult;
import com.citc.nce.robot.res.GatewayTokenRes;
import com.citc.nce.robot.res.SendMessageRes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class WalnutGatewayClient{

    @Value("${5g-restful.signatures.auth.appHost:https://www.baidu.com}")
    protected String host;
    @Value("${5g-restful.signatures.auth.appId:no_config}")
    protected String appId;
    @Value("${5g-restful.signatures.auth.appKey:no_config}")
    protected String appKey;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    private static final String accessTokenKey = "5g:auth:accessToken:key:";

    /**
     * 发送消息
     * @param message
     * @return
     * @throws Exception
     */
    public RestResult<SendMessageRes> sendMessage(Object message,String appId,String appKey) throws BizException {
        try{
            String url = host + "/walnut/v1/sendMessage";
            HttpRequest request = HttpUtil.createRequest(Method.POST,url);
            Map<String, String> headers = new HashMap<>();
            headers.put("Accept", "application/json");
            headers.put("accessToken",getToken());
            request.addHeaders(headers);
            request.body(JSON.toJSONString(message));
            HttpResponse response = request.execute();
            String responseData = response.body();
            if(responseData!=null&&"0".equals(readPathValue(responseData,"$.code"))){
                return RestResult.success(JSONObject.parseObject(responseData,SendMessageRes.class));
            }else{
                Integer code = readPathValue(responseData,"$.code")!=null?Integer.parseInt(readPathValue(responseData,"$.code")):-1;
                return RestResult.error(code,readPathValue(responseData,"$.message"));
            }
        }catch (BizException e){
            throw e;
        }catch (Exception e){
            throw new BizException(GlobalErrorCode.SERVER_BUSY.getCode(),"调用5G gateway发送消息失败："+e);
        }


    }

    /**
     * 获取AccessToken
     * @return
     * @throws BizException
     */
    protected String getToken() throws BizException{
        //先从缓存获取
        Object accessToken = redisTemplate.opsForValue().get(accessTokenKey);
        if(accessToken!=null){
            return accessToken.toString();
        }
        String url = host + "/walnut/v1/accessToken";
        String timestamp = String.valueOf(System.currentTimeMillis());
        String nonce = RandomStringUtils.random(8);
        String sign = DigestUtil.sha256Hex(appId+appKey+timestamp+nonce);
        Map<String,Object> params = new HashMap<>();
        params.put("appid",appId);
        params.put("nonce",timestamp+nonce);
        params.put("sign",sign);
        String body = HttpUtil.post(url,params,30000);
        GatewayTokenRes tokenRes = JSONObject.parseObject(body, GatewayTokenRes.class);
        if(!tokenRes.isSuccess()){
            throw new BizException(GlobalErrorCode.SERVER_BUSY.getCode(),"获取5G gateway token错误："+tokenRes.getMessage());
        }
        setToken(tokenRes);
        return tokenRes.getToken();
    }

    public Long ttl() {
        return redisTemplate.getExpire(accessTokenKey, TimeUnit.SECONDS);
    }

    /**
     * 缓存token
     * @param oauthTokenRes
     */
    private void setToken(GatewayTokenRes oauthTokenRes) {
        //缓存Token
        redisTemplate.opsForValue().set(accessTokenKey , oauthTokenRes.getToken());
        redisTemplate.expire(accessTokenKey , 7200, TimeUnit.SECONDS);
    }

    private String readPathValue(String json, String path) {
        Object read = JSONPath.read(json, path);
        return read == null ? null : read.toString();
    }

}
