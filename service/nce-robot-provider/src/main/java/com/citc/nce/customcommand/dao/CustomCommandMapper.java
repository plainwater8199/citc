package com.citc.nce.customcommand.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.customcommand.entity.CustomCommand;
import com.citc.nce.customcommand.enums.CustomCommandType;
import com.citc.nce.customcommand.vo.CustomCommandSimpleVo;
import com.citc.nce.customcommand.vo.MyAvailableCustomCommandVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 自定义指令 Mapper 接口
 * </p>
 *
 * @author jcrenc
 * @since 2023-11-09 02:53:48
 */
public interface CustomCommandMapper extends BaseMapper<CustomCommand> {

    Page<CustomCommandSimpleVo> searchCommand(Page<CustomCommandSimpleVo> page);

    Page<CustomCommandSimpleVo> searchPublishCommand(@Param("type") CustomCommandType type, Page<CustomCommandSimpleVo> page);

    Page<MyAvailableCustomCommandVo> getMyAvailableCommand(@Param("customerId") String customerId, @Param("needContent") Boolean needContent, Page<MyAvailableCustomCommandVo> page);

    Page<MyAvailableCustomCommandVo> getMyCommand(@Param("customerId") String customerId, @Param("needContent") Boolean needContent, Page<MyAvailableCustomCommandVo> page);

    void updateMssIDNullById(@Param("ids") List<Long> ids);

    List<CustomCommand> searchListForMS();
}
