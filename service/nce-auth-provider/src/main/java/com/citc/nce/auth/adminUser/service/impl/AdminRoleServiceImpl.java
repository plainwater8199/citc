package com.citc.nce.auth.adminUser.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.adminUser.dao.AdminRoleDao;
import com.citc.nce.auth.adminUser.dao.AdminUserMapper;
import com.citc.nce.auth.adminUser.dao.AdminUserRoleDao;
import com.citc.nce.auth.adminUser.entity.AdminMenuDo;
import com.citc.nce.auth.adminUser.entity.AdminRoleDo;
import com.citc.nce.auth.adminUser.entity.AdminUserRoleDo;
import com.citc.nce.auth.adminUser.service.AdminRoleService;
import com.citc.nce.auth.adminUser.vo.req.DisableOrEnableReq;
import com.citc.nce.auth.adminUser.vo.req.OperatorReq;
import com.citc.nce.auth.adminUser.vo.req.RoleReq;
import com.citc.nce.auth.constant.AuthError;
import com.citc.nce.auth.user.vo.req.CodeReq;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.misc.constant.NumCode;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * <p>
 * 运营人员角色表 服务实现类
 * </p>
 *
 * @author author
 * @since 2022-09-23
 */
@Service
public class AdminRoleServiceImpl extends ServiceImpl<AdminRoleDao, AdminRoleDo> implements AdminRoleService {

    @Resource
    private AdminRoleDao adminRoleDao;
    @Resource
    private AdminUserMapper adminUserMapper;
    @Resource
    private AdminUserRoleDao adminUserRoleDao;
    @Resource
    private AdminUserServiceImpl adminUserServiceImpl;
    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public PageResult getRolePage(OperatorReq req) {
        PageParam page = new PageParam();
        page.setPageNo(req.getPageNo());
        page.setPageSize(req.getPageSize());
        LambdaQueryWrapperX<AdminRoleDo> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.likeIfPresent(AdminRoleDo::getRoleName, req.getKeyWord())
                .eqIfPresent(AdminRoleDo::getRoleStatus, req.getStatus())
                .inIfPresent(AdminRoleDo::getRoleId, req.getRoleIdList())
                .orderByDesc(AdminRoleDo::getCreateTime);
        return adminRoleDao.selectPage(page, queryWrapper);
    }

    @Override
    @Transactional()
    public void enableOrDisableRole(DisableOrEnableReq req) {
        LambdaUpdateWrapper<AdminRoleDo> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AdminRoleDo::getRoleId, req.getCode());
        AdminRoleDo adminRoleDo = new AdminRoleDo().setRoleStatus(req.getStatus());
        if (!update(adminRoleDo, wrapper))
            throw new BizException(AuthError.Execute_SQL_UPDATE);
        List<AdminUserRoleDo> roleList = adminUserRoleDao.selectList(AdminUserRoleDo::getRoleId, req.getCode());
        //刷新redis里面用户权限
        if (CollectionUtils.isNotEmpty(roleList)) {
            roleList.forEach(i -> {
                List<AdminMenuDo> data = adminUserMapper.selectMenuByUserId(i.getUserId());
                Map<String, List<String>> url = adminUserServiceImpl.userMenu(data);
                redisTemplate.opsForValue().set("USER_ID:" + i.getUserId(), url);
            });
        }
    }

    @Override
    public void editRole(RoleReq req) {
        AdminRoleDo role = adminRoleDao.selectOne(AdminRoleDo::getRoleName, req.getRoleName());
        AdminRoleDo adminRoleDo = new AdminRoleDo();
        BeanUtils.copyProperties(req, adminRoleDo);
        if (StringUtils.isNotBlank(req.getRoleId())) {
            if (null != role && !req.getRoleId().equalsIgnoreCase(role.getRoleId()))
                throw new BizException(AuthError.ROLENAME_EXISTS);
            LambdaUpdateWrapper<AdminRoleDo> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(AdminRoleDo::getRoleId, req.getRoleId());
            if (NumCode.ONE.getCode() != adminRoleDao.update(adminRoleDo, wrapper))
                throw new BizException(AuthError.Execute_SQL_UPDATE);
        } else {
            if (null != role) throw new BizException(AuthError.ROLENAME_EXISTS);
            LambdaQueryWrapperX<AdminRoleDo> queryWrapper = new LambdaQueryWrapperX<>();
            queryWrapper.eq(AdminRoleDo::getRoleName, req.getRoleName());
            AdminRoleDo roleDo = adminRoleDao.selectOne(queryWrapper);
            if (null != roleDo) throw new BizException(AuthError.ROLE_REPETITION);
            adminRoleDo.setRoleId(UUID.randomUUID().toString().replaceAll("-", ""))
                    .setRoleStatus(NumCode.ONE.getCode());
            if (!save(adminRoleDo)) throw new BizException(AuthError.Execute_SQL_SAVE);
        }
    }

    @Transactional()
    @Override
    public void removeRole(CodeReq req) {
        //查询角色受影响的所有userId
        List<AdminUserRoleDo> roleList = adminUserRoleDao.selectList(AdminUserRoleDo::getRoleId, req.getCode());
        adminUserMapper.deleteRole(req);
        //刷新redis里面用户权限
        if (CollectionUtils.isNotEmpty(roleList)) {
            roleList.forEach(i -> {
                List<AdminMenuDo> data = adminUserMapper.selectMenuByUserId(i.getUserId());
                Map<String, List<String>> url = adminUserServiceImpl.userMenu(data);
                redisTemplate.opsForValue().set("USER_ID:" + i.getUserId(), url);
            });
        }
    }
}
