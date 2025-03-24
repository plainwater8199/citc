package com.citc.nce.aim.dao;

import com.citc.nce.aim.dto.AimProjectOrderInfoDto;
import com.citc.nce.aim.dto.AimProjectQueryDto;
import com.citc.nce.aim.entity.AimProjectDo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;

import java.util.List;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 18:12
 */
public interface AimProjectDao extends BaseMapperX<AimProjectDo> {

    Long getMaxId();

    int queryProjectListCount(AimProjectQueryDto dto);

    List<AimProjectOrderInfoDto> queryProjectList(AimProjectQueryDto dto);

    List<AimProjectOrderInfoDto> queryProjectEnabledOrderInfo();
}
