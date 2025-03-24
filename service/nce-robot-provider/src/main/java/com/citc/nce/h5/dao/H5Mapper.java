package com.citc.nce.h5.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.citc.nce.h5.entity.H5Do;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author mac
 * @since 2023-05-21
 */
public interface H5Mapper extends BaseMapper<H5Do> {

    void updateMssIDNullById(@Param("h5IdList") List<Long> h5IdList);
}
