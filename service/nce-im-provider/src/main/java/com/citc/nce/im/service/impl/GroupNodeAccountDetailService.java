package com.citc.nce.im.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.im.entity.GroupNodeAccountDetail;
import com.citc.nce.im.mapper.GroupNodeAccountDetailMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author jcrenc
 * @since 2024/5/16 15:20
 */
@Service
public class GroupNodeAccountDetailService extends ServiceImpl<GroupNodeAccountDetailMapper, GroupNodeAccountDetail> implements IService<GroupNodeAccountDetail> {

    public void clearByNodeIds(List<Long> nodeIds) {
        remove(new LambdaQueryWrapper<GroupNodeAccountDetail>().in(GroupNodeAccountDetail::getNodeId, nodeIds));
    }
}
