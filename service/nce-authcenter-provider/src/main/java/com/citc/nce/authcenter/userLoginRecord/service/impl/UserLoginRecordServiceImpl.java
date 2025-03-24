package com.citc.nce.authcenter.userLoginRecord.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.citc.nce.authcenter.userLoginRecord.dao.UserLoginRecordDao;
import com.citc.nce.authcenter.userLoginRecord.entity.UserLoginRecordDo;
import com.citc.nce.authcenter.userLoginRecord.service.UserLoginRecordService;
import com.citc.nce.authcenter.userLoginRecord.vo.FindUserReq;
import com.citc.nce.authcenter.userLoginRecord.vo.FindUserResp;
import com.citc.nce.authcenter.userLoginRecord.vo.QueryUserLoginRecordReq;
import com.citc.nce.authcenter.userLoginRecord.vo.QueryUserLoginRecordResp;
import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/27 16:15
 */
@Service
@Slf4j
public class UserLoginRecordServiceImpl implements UserLoginRecordService {

    @Autowired
    UserLoginRecordDao userLoginRecordDao;

    @Override
    public PageResult<QueryUserLoginRecordResp> queryList(QueryUserLoginRecordReq req) {
        PageResult<QueryUserLoginRecordResp> res = new PageResult<>();
        PageParam page = new PageParam();
        page.setPageSize(req.getPageSize());
        page.setPageNo(req.getPageNo());
        LambdaQueryWrapperX<UserLoginRecordDo> queryWrapperX = new LambdaQueryWrapperX<>();
        if (StringUtils.isNotEmpty(req.getUserId())) {
            queryWrapperX.eq(UserLoginRecordDo::getUserId, req.getUserId());
        }
        if (StringUtils.isNotEmpty(req.getCspAccount())){
            queryWrapperX.eq(UserLoginRecordDo::getCspAccount, req.getCspAccount());
            // 追加条件 CSP自己不作为统计对象
            queryWrapperX.apply("csp_account != user_id");
        }
        if (null != req.getPlatformType()) {
            queryWrapperX.eq(UserLoginRecordDo::getPlatformType, req.getPlatformType());
        }
        if (null != req.getStartTime()) {
            queryWrapperX.ge(UserLoginRecordDo::getCreateTime, req.getStartTime());
        }
        if (null != req.getEndTime()) {
            queryWrapperX.le(UserLoginRecordDo::getCreateTime, req.getEndTime());
        }

        if (-1 == req.getPageSize()) {
            List<UserLoginRecordDo> userLoginRecordDoList = userLoginRecordDao.selectList(queryWrapperX);
            getResultList(res, userLoginRecordDoList);
            return res;
        }
        PageResult<UserLoginRecordDo> loginRecordDoPageResult = userLoginRecordDao.selectPage(page, queryWrapperX);
        getResultList(res, loginRecordDoPageResult.getList());

        return res;
    }

    @Override
    public FindUserResp findUser(FindUserReq req) {
        LambdaQueryWrapper<UserLoginRecordDo> queryWrapper = Wrappers.<UserLoginRecordDo>lambdaQuery()
                .eq(UserLoginRecordDo::getUserId, req.getUserId())
                .eq(UserLoginRecordDo::getPlatformType, req.getPlatformType())
                .orderByDesc(UserLoginRecordDo::getCreateTime)
                .last("limit 1");
        List<UserLoginRecordDo> list = userLoginRecordDao.selectList(queryWrapper);
        if(CollectionUtils.isNotEmpty(list)){
            return BeanUtil.copyProperties(list.get(0),FindUserResp.class);
        }
        return null;
    }

    private static void getResultList(PageResult<QueryUserLoginRecordResp> res, List<UserLoginRecordDo> inputList) {
        if (CollectionUtils.isNotEmpty(inputList)) {
            List<QueryUserLoginRecordResp> list = new ArrayList<>();
            for (UserLoginRecordDo recordDo:
                    inputList) {
                QueryUserLoginRecordResp resp = new QueryUserLoginRecordResp();
                BeanUtils.copyProperties(recordDo, resp);
                list.add(resp);
            }
            res.setList(list);
        }
        res.setTotal(Long.valueOf(inputList.size()));
    }
}
