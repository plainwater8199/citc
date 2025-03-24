package com.citc.nce.auth.user.dao;

import com.citc.nce.auth.adminUser.vo.req.ManageUserReq;
import com.citc.nce.auth.adminUser.vo.resp.UserPageListResp;
import com.citc.nce.auth.user.vo.req.PlatformApplicationReviewReq;
import com.citc.nce.auth.user.vo.req.UserIdReq;
import com.citc.nce.auth.user.vo.req.UserPageListDBVO;
import com.citc.nce.auth.user.vo.resp.UserIdAndNameResp;
import com.citc.nce.auth.user.vo.resp.UserResp;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/8/12 13:52
 * @Version 1.0
 * @Description:
 */

@Mapper
public interface UserMapper {

    List<UserPageListResp> getEnergyMallUserList(UserPageListDBVO userPageListDBVO);

    Long getEnergyMallUserCount(UserPageListDBVO userPageListDBVO);

    List<UserResp> getChatBotAndHardWalnutsUserList(ManageUserReq req);

    Long getChatBotAndHardWalnutsUserCount(ManageUserReq req);

    List<UserResp> getgetPlatformApplicationReviewList(PlatformApplicationReviewReq req);

    Long getPlatformApplicationReviewCount(PlatformApplicationReviewReq req);

    List<UserResp> getUniteManageUserList(ManageUserReq req);

    Long getUniteManageUserCount(ManageUserReq req);

    List<UserIdAndNameResp> selectUserInfoByUserId(UserIdReq req);
}
