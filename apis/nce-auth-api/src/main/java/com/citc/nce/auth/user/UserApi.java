package com.citc.nce.auth.user;

import com.citc.nce.auth.user.vo.req.*;
import com.citc.nce.auth.user.vo.resp.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/6/21 17:06
 * @Version: 1.0
 * @Description:
 */
@Api(tags = "门户用户模块")
@FeignClient(value = "auth-service", contextId = "auth", url = "${auth:}")
public interface UserApi {

    @ApiOperation("获取企业认证用户的信息(chatbot)")
    @PostMapping("/user/getEnterpriseUserListForChatbot")
    List<UserResp> getEnterpriseUserListForChatbot();

    @ApiOperation("通过手机号查询")
    @PostMapping("/user/findByPhone")
    UserResp findByPhone(@RequestParam("phone") String phone);
}

