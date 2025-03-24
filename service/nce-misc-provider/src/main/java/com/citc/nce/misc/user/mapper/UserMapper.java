package com.citc.nce.misc.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @BelongsProject: chatbot
 * @BelongsPackage: com.citc.nce.misc.user
 * @Author: weilanglang
 * @CreateTime: 2022-11-16  14:40
 
 * @Version: 1.0
 */
@Mapper
public interface UserMapper {
    String findByName(@Param("name") String name);
}
