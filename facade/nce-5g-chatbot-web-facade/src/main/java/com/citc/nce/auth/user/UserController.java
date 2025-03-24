package com.citc.nce.auth.user;

import com.citc.nce.auth.user.vo.req.UserDetailReq;
import com.citc.nce.auth.user.vo.resp.UserDetailResp;
import com.citc.nce.auth.user.vo.resp.UserResp;
import com.citc.nce.common.core.pojo.BaseUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/6/22 16:18
 * @Version: 1.0
 * @Description:
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserApi userApi;
//
//    @PostMapping("/detailById")
//    public UserDetailResp userDetail(@RequestBody UserDetailReq userDetailReq, BaseUser baseUser) {
//        log.info("facade  user controller enter");
//        UserDetailResp detail = userApi.detail(userDetailReq);
//        return detail;
//    }

    @GetMapping("/getEnterpriseUserListForChatbot")
    public List<UserResp> getEnterpriseUserListForChatbot(){
        return userApi.getEnterpriseUserListForChatbot();
    }
}
