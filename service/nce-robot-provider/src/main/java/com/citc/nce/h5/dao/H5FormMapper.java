package com.citc.nce.h5.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.citc.nce.h5.dto.H5FormCountDto;
import com.citc.nce.h5.entity.H5FormDo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author mac
 * @since 2023-05-21
 */
public interface H5FormMapper extends BaseMapper<H5FormDo> {

    List<H5FormCountDto> queryCountByH5Ids(@Param("h5IdList") List<Long> h5IdList);
}
