package com.citc.nce.auth.user;

import com.citc.nce.auth.user.entity.UserDo;
import com.citc.nce.auth.user.service.UserService;
import com.citc.nce.auth.user.vo.req.*;
import com.citc.nce.auth.user.vo.resp.UserInfoResp;
import com.citc.nce.auth.user.vo.resp.UserRegisterResp;
import com.citc.nce.auth.user.vo.resp.UserResp;
import com.citc.nce.authcenter.auth.AdminAuthApi;
import com.citc.nce.authcenter.auth.vo.req.UpdateUserViolationReq;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.util.SessionContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/6/21 17:11
 * @Version: 1.0
 * @Description:
 */
@RestController()
@Slf4j
public class UserController implements UserApi {

    @Resource
    private UserService userService;

    @Override
    public List<UserResp> getEnterpriseUserListForChatbot() {
        return userService.getEnterpriseUserListForChatbot();
    }


    @Override
    public UserResp findByPhone(String phone) {
        return userService.findByPhone(phone);
    }
}
