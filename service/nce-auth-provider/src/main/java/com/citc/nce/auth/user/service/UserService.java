package com.citc.nce.auth.user.service;


import com.citc.nce.auth.adminUser.vo.req.DeleteClientUserReq;
import com.citc.nce.auth.user.entity.UserDo;
import com.citc.nce.auth.user.vo.resp.UserInfo;
import com.citc.nce.auth.user.vo.resp.UserResp;
import com.citc.nce.authcenter.auth.vo.req.RegisterReq;

import java.util.List;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/6/21 17:22
 * @Version: 1.0
 * @Description:
 */
public interface UserService {


    //账户名/邮箱/手机号是否通过唯一校验：true   false
    Boolean checkUnique(String name, Integer type, String userId);


    //根据userId 获取用户基本信息
    UserDo userInfoDetailByUserId(String userId);


    //更新用户认证状态
    void updateUserAuthStatus(String userId);


    void deleteClientUserInfo(DeleteClientUserReq req);


    //通过userId刷新用户名称
    void updateUserName(String name, String userId);


    //更新用户基本基本信息
    void updateUserInfo(String userId, UserDo userDo);


    List<UserResp> getEnterpriseUserListForChatbot();


    UserInfo getUserBaseInfoByUserId(String userId);


    UserDo saveForRegister(RegisterReq req);

    UserResp findByPhone(String phone);
}
