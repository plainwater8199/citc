package com.citc.nce.authcenter.user.service;

import com.citc.nce.authcenter.user.entity.UserDo;

import java.util.List;
import java.util.Map;

public interface UserService {
    /**
     * 用户保存
     * @param userDo 用户信息
     * @return 返回结果
     */
    UserDo saveUser(UserDo userDo);

    /**
     * 查询用户--通过用户名或者手机号
     * @param name 用户名
     * @param phone 手机号
     * @return 用户信息
     */
    UserDo findUserByNameOrPhoneOrEmail(String name, String phone, String email);


    /**
     * 通过手机号查询
     * @param phone 手机号
     * @return 用户信息
     */
    UserDo findByPhone(String phone);

    void updateUserName(String enterpriseAccountName, String clientUserId);

    /**
     * 根据用户ID查询用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    UserDo userInfoDetailByUserId(String userId);

    /**
     * 更新用户信息
     * @param userId 用户id
     * @param userDo 用户信息
     */
    void updateUserInfo(String userId, UserDo userDo);

    /**
     * 获取所有有效的用户ID
     * @return 有效的用户ID
     */
    List<String> findAllActiveUser();
    /**
     * 根据用户ids查询用户名称map
     * @return 用户名称map
     */
    Map<String, String> findUserNameByIds(List<String> userIds);

    /**
     * 用户列表查询
     * @param userIds 用户id列表
     * @return 用户类别
     */
    List<UserDo> findUserByIds(List<String> userIds);

    /**
     * 根据用户ID查询用户
     * @param uuid 用户id
     * @return 用户
     */
    UserDo findByUserId(String uuid);


    void userStatusSyn();

    /**
     * 根据Email查询用户
     * @param email 邮箱
     * @return 用户信息
     */
    UserDo findByEmail(String email);


    UserDo findByAccount(String account);
}
