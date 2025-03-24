package com.citc.nce.authcenter.user.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.authcenter.auth.vo.*;
import com.citc.nce.authcenter.tempStorePerm.bean.ChangePrem;
import com.citc.nce.authcenter.user.entity.UserDo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/8/12 13:52
 * @Version 1.0
 * @Description:
 */

@Mapper
public interface UserMapper extends BaseMapper<UserDo> {

    List<UserPageInfo> getEnergyMallUserList(UserPageDBInfo userPageDBInfo);

    Long getEnergyMallUserCount(UserPageDBInfo userPageDBInfo);

    List<UserInfo> getChatBotAndHardWalnutsUserList(ManageUserInfo req);

    Long getChatBotAndHardWalnutsUserCount(ManageUserInfo req);

    List<UserInfo> getgetPlatformApplicationReviewList(PlatformApplicationReviewInfo req);

    Long getPlatformApplicationReviewCount(PlatformApplicationReviewInfo req);

    /**
     * 统一用户管理查询
     */
    Page<UserInfo> queryUnifiedManageUser(@Param("name") String name, @Param("phone") String phone, @Param("personAuthStatus") Integer personAuthStatus, @Param("enterpriseAuthStatus") Integer enterpriseAuthStatus, Page<UserInfo> page);


    List<UserIdAndNameInfo> selectUserInfoByUserId(UserIdInfo req);

    /**
     * 社区用户列表查询
     *
     * @param manageUserInfo 筛选条件
     * @return 用户列表
     */
    List<UserInfo> getCommunityUserList(ManageUserInfo manageUserInfo);

    /**
     * 社区用户总数查询
     *
     * @param manageUserInfo 筛选条件
     * @return 总数
     */
    Long getCommunityUserListCount(ManageUserInfo manageUserInfo);

    /**
     * csp用户查询
     *
     * @param manageUserInfo 筛选条件
     * @return 总数
     */
    List<UserInfo> getCSPUserList(ManageUserInfo manageUserInfo);

    /**
     * csp用户总数查询
     *
     * @param manageUserInfo 筛选条件
     * @return 总数
     */
    Long getCSPUserCount(ManageUserInfo manageUserInfo);

    /**
     * 商城用户查询
     *
     * @param userPageDBInfo 筛选条件
     * @return 总数
     */
    List<UserPageInfo> getMallUserList(UserPageDBInfo userPageDBInfo);

    /**
     * 商城用户总数查询
     *
     * @param userPageDBInfo 筛选条件
     * @return 总数
     */
    Long getMallUserListCount(UserPageDBInfo userPageDBInfo);

    /**
     * 修改扩展商城权限
     *
     * @param changePrem 权限
     */
    void changeTempStorePermission(ChangePrem changePrem);


    Integer getTempStorePerm(String userId);

    /**
     * 返回手机号是否已经被csp注册
     *
     * @param phone 手机号
     * @return 已被注册返回true
     */
    Boolean existsByCspPhone(@Param("phone") String phone);

    /**
     * 修改csp的套餐状态
     *
     * @param userId 用户id
     * @param status 状态
     */
    void changeCspMealStatusByUserId(@Param("userId") String userId, @Param("status") int status);

    void changeCspMealStatusByCspId(@Param("cspId") String cspId, @Param("status") Integer status);

    /**
     * 根据cspId查询状态
     *
     * @param cspId cspId 为空查询全部
     */
    List<UserInfo> listMealStatus(@Param("cspId") String cspId, @Param("mealStatus") Integer mealStatus);
}
