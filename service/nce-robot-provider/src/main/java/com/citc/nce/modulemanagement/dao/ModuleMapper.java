package com.citc.nce.modulemanagement.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.citc.nce.keywordsreply.entity.KeywordsReply;
import com.citc.nce.modulemanagement.entity.ModuleDo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author jcrenc
 * @since 2024/5/29 15:27
 */
public interface ModuleMapper extends BaseMapper<ModuleDo> {
    void updateMssIDNullById(@Param("ids") List<String> ids);
}
