package com.citc.nce.auth.adminUser.service.impl;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.adminUser.dao.AdminUserDao;
import com.citc.nce.auth.adminUser.dao.AdminUserMapper;
import com.citc.nce.auth.adminUser.dao.AdminUserRoleDao;
import com.citc.nce.auth.adminUser.entity.AdminMenuDo;
import com.citc.nce.auth.adminUser.entity.AdminUserDo;
import com.citc.nce.auth.adminUser.entity.AdminUserRoleDo;
import com.citc.nce.auth.adminUser.service.AdminUserRoleService;
import com.citc.nce.auth.adminUser.vo.req.EditOperatorReq;
import com.citc.nce.auth.adminUser.vo.req.UserAndRoleReq;
import com.citc.nce.auth.constant.AuthError;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.misc.constant.NumCode;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 运营人员用户的角色表 服务实现类
 *
 * @author ylzouf
 * @date 2022/09/23
 */
@Service
public class AdminUserRoleServiceImpl extends ServiceImpl<AdminUserRoleDao, AdminUserRoleDo> implements AdminUserRoleService {

    @Resource
    private AdminUserDao adminUserDao;
    @Resource
    private AdminUserRoleDao adminUserRoleDao;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private AdminUserMapper adminUserMapper;
    @Resource
    private AdminUserServiceImpl adminUserServiceImpl;
    private static final Pattern p = Pattern.compile("[\u4e00-\u9fa5]");



    @Override
    @Transactional()
    public void editOperator(EditOperatorReq req) {
        Matcher m = p.matcher(req.getAccountName().trim());
        if (m.find()) throw new BizException(AuthError.CAN_NOT_HAVE_CHINESE);
        AdminUserDo userAccountName = adminUserDao.selectOne(AdminUserDo::getAccountName, req.getAccountName());
        AdminUserDo userPhone = adminUserDao.selectOne(AdminUserDo::getPhone, req.getPhone());
        String userId;
        if (StringUtils.isNotBlank(req.getUserId())) {//有该运营人员
            if (null != userAccountName && !req.getUserId().equalsIgnoreCase(userAccountName.getUserId()))
                throw new BizException(AuthError.ACCOUNTNAME_EXISTS);
            if (null != userPhone && !req.getUserId().equalsIgnoreCase(userPhone.getUserId()))
                throw new BizException(AuthError.PHONE_EXISTS);
            userId = req.getUserId();

            LambdaUpdateWrapper<AdminUserDo> wrapper = new LambdaUpdateWrapper<>();
            AdminUserDo user = new AdminUserDo()
                    .setAccountName(req.getAccountName())
                    .setFullName(req.getFullName())
                    .setPhone(req.getPhone());
            wrapper.eq(AdminUserDo::getUserId, userId);
            if (NumCode.ONE.getCode() != adminUserDao.update(user, wrapper))
                throw new BizException(AuthError.Execute_SQL_UPDATE);
            if (CollectionUtils.isNotEmpty(req.getRoleIdList())) {
                List<AdminUserRoleDo> adminUserRoleList = adminUserRoleDao.selectList(AdminUserRoleDo::getUserId, req.getUserId());
                List<String> oldRoleIdList = adminUserRoleList.stream().map(AdminUserRoleDo::getRoleId).collect(Collectors.toList());
                List<String> newRoleIdList = req.getRoleIdList();
                List<String> removeRoleIdList = oldRoleIdList.stream().filter(i -> !newRoleIdList.contains(i)).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(removeRoleIdList)) {
                    if (adminUserMapper.deleteRoleList(removeRoleIdList) < NumCode.ONE.getCode())
                        throw new BizException(AuthError.Execute_SQL);
                }
                List<String> saveRoleIdList = newRoleIdList.stream().filter(i -> !oldRoleIdList.contains(i)).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(saveRoleIdList)) {
                    List<AdminUserRoleDo> list = new ArrayList<>();
                    saveRoleIdList.forEach(i -> list.add(new AdminUserRoleDo().setRoleId(i).setUserId(userId)));
                    if (!saveBatch(list)) throw new BizException(AuthError.Execute_SQL);
                }
            } else {
                adminUserMapper.deleteRoleByUserId(req.getUserId());
            }
        } else {
            userId = UUID.randomUUID().toString();
            if (null != userAccountName) throw new BizException(AuthError.ACCOUNTNAME_EXISTS);
            if (null != userPhone) throw new BizException(AuthError.PHONE_EXISTS);

            AdminUserDo adminUserDo = new AdminUserDo();
            BeanUtils.copyProperties(req, adminUserDo);
            adminUserDo.setUserStatus(NumCode.ONE.getCode()).setUserId(userId);
            if (NumCode.ONE.getCode() != adminUserDao.insert(adminUserDo))
                throw new BizException(AuthError.Execute_SQL_SAVE);
            if (CollectionUtils.isNotEmpty(req.getRoleIdList())) {
                List<AdminUserRoleDo> list = new ArrayList<>();
                req.getRoleIdList().forEach(i -> list.add(new AdminUserRoleDo().setRoleId(i).setUserId(userId)));
                if (!saveBatch(list)) throw new BizException(AuthError.Execute_SQL_SAVE);
            }
        }
        //查询该用户所有资源刷新redis里面数据
        List<AdminMenuDo> data = adminUserMapper.selectMenuByUserId(userId);
        Map<String, List<String>> url = adminUserServiceImpl.userMenu(data);
        redisTemplate.opsForValue().set("USER_ID:" + userId, url);
    }

    @Override
    @Transactional()
    public void roleConfigurationMember(UserAndRoleReq req) {
        if ("admin".equalsIgnoreCase(req.getRoleId())) req.getUserIdList().add("admin");
        //根据角色查询所有成员
        List<AdminUserRoleDo> roleList = adminUserRoleDao.selectList(AdminUserRoleDo::getRoleId, req.getRoleId());
        //所有受影响的userid
        List<String> userList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(roleList)) {
            List<String> oldUserIdList = roleList.stream().map(AdminUserRoleDo::getUserId).collect(Collectors.toList());
            List<String> newUserIdList = req.getUserIdList();
            userList = Stream.of(oldUserIdList, newUserIdList).flatMap(Collection::stream).distinct().collect(Collectors.toList());
            //求差集(删除)
            List<String> removeList = oldUserIdList.stream().filter(i -> !newUserIdList.contains(i)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(removeList)) {
                if (adminUserMapper.deleteRoleByUserIdList(req.getRoleId(), removeList) < NumCode.ONE.getCode())
                    throw new BizException(AuthError.Execute_SQL_DELETE);
            }
            List<String> saveList = newUserIdList.stream().filter(i -> !oldUserIdList.contains(i)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(saveList)) {
                List<AdminUserRoleDo> list = new ArrayList<>();
                saveList.forEach(i -> list.add(new AdminUserRoleDo().setRoleId(req.getRoleId()).setUserId(i)));
                if (!saveBatch(list)) throw new BizException(AuthError.Execute_SQL_SAVE);
            }
        } else {
            if (CollectionUtils.isNotEmpty(req.getUserIdList())) {
                userList = req.getUserIdList();
                List<AdminUserRoleDo> list = new ArrayList<>();
                req.getUserIdList().forEach(i -> list.add(new AdminUserRoleDo().setRoleId(req.getRoleId()).setUserId(i)));
                if (!saveBatch(list)) throw new BizException(AuthError.Execute_SQL_SAVE);
            }
        }
        //刷新redis用户权限
        if (CollectionUtils.isNotEmpty(userList)) {
            userList.forEach(i -> {
                List<AdminMenuDo> data = adminUserMapper.selectMenuByUserId(i);
                Map<String, List<String>> url = adminUserServiceImpl.userMenu(data);
                redisTemplate.opsForValue().set("USER_ID:" + i, url);
            });
        }
    }
}
