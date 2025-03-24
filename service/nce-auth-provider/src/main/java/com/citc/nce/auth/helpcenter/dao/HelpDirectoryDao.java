package com.citc.nce.auth.helpcenter.dao;

import com.citc.nce.auth.helpcenter.entity.HelpDirectoryDo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Select;

/**
 * @author yy
 * @date 2024-05-11 11:07:29
 */
public interface HelpDirectoryDao extends BaseMapperX<HelpDirectoryDo> {
    @Select("select max(version) from help_directory")
    Integer selectMaxVersion();
    @Select("select * from help_directory where record_type=1 and deleted=0 order by publish_time desc limit 1")
    HelpDirectoryDo getLastPublished();
}
