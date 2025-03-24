package com.citc.nce.robotfile.mapper;


import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import com.citc.nce.robotfile.entity.GroupDo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年6月29日16:29:48
 * @Version: 1.0
 * @Description: GroupMapper
 */
@Repository
public interface GroupMapper extends BaseMapperX<GroupDo> {

    boolean updateGroup(@Param(value = "group_name") String groupName, @Param(value = "deleted") Integer deleted, @Param(value = "id") Long id);

}
