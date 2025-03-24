package com.citc.nce.aim.privatenumber.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface TableDao {

    @Select("SELECT table_name " +
            "FROM information_schema.tables " +
            "WHERE table_schema = #{schema} " +
            "AND table_name LIKE CONCAT(#{prefix},'%')")
    List<String> selectTablesByPrefix(@Param("schema") String schema, @Param("prefix") String prefix);
}
