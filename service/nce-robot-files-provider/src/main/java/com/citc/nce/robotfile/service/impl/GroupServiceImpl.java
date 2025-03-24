package com.citc.nce.robotfile.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.dto.GroupReq;
import com.citc.nce.mybatis.core.entity.BaseDo;
import com.citc.nce.robotfile.configure.SuperAdministratorUserIdConfigure;
import com.citc.nce.robotfile.entity.GroupDo;
import com.citc.nce.robotfile.exp.RobotFileExp;
import com.citc.nce.robotfile.mapper.GroupMapper;
import com.citc.nce.robotfile.mapper.PictureMapper;
import com.citc.nce.robotfile.service.IGroupService;
import com.citc.nce.vo.GroupResp;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: wenliuch
 * @Date: 2022年6月29日16:29:48
 * @Version: 1.0
 * @Description: GroupServiceImpl
 */
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(SuperAdministratorUserIdConfigure.class)
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDo> implements IGroupService {

    @Resource
    private GroupMapper groupMapper;


    @Resource
    private PictureMapper pictureMapper;

    private final SuperAdministratorUserIdConfigure superAdministratorUserIdConfigure;


    @Override
    public List<GroupResp> selectAll() {
        return change();
    }

    @Override
    public void saveGroup(String groupName) {
        GroupDo group = new GroupDo();
        group.setGroupName(groupName);
        if (!save(group)) throw new BizException(RobotFileExp.SQL_ERROR);
    }

    @Override
    public void updateGroup(List<GroupReq> vos) {
        String userId = SessionContextUtil.getUser().getUserId();
        List<GroupDo> pictureDos = groupMapper.selectList(
                new LambdaQueryWrapper<GroupDo>().in(BaseDo::getId, vos.stream().map(GroupReq::getId).collect(Collectors.toSet())));
        if (!pictureDos.stream().allMatch(s -> s.getCreator().equals(userId))) {
            throw new BizException("不能修改别人的资源");
        }
        vos.forEach(groupDto -> {
            groupMapper.updateGroup(groupDto.getGroupName(), groupDto.getDeleted(), groupDto.getId());
            //将涉及到图片groupId全部设置为0
            if (groupDto.getDeleted() == 1) {
                pictureMapper.updateGroupId(groupDto.getId());
            }
        });
    }

    @Override
    public List<GroupResp> manage() {
        String userId = SessionContextUtil.getUser().getUserId();
        if (StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
            return pictureMapper.countByGroupId("");
        }
        return pictureMapper.countByGroupId(userId);
    }

    public List<GroupResp> change() {
        QueryWrapper<GroupDo> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", 0);
        String userId = SessionContextUtil.getUser().getUserId();
        if (!StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
            wrapper.eq("creator", userId);
        }
        List<GroupDo> groups = groupMapper.selectList(wrapper);
        List<GroupResp> vos = new ArrayList<>();
        groups.forEach(group -> {
            GroupResp vo = new GroupResp();
            vo.setGroupName(group.getGroupName());
            vo.setId(group.getId());
            vos.add(vo);
        });
        return vos;
    }
}
