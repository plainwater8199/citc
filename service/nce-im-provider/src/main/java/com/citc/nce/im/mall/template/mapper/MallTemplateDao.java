package com.citc.nce.im.mall.template.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.im.mall.template.entity.MallTemplateDo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import com.citc.nce.robot.api.mall.template.vo.req.MallTemplateQueryRobotListReq;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>TODO</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 18:12
 */
public interface MallTemplateDao extends BaseMapperX<MallTemplateDo> {
    String queryName(@Param("uuid") String uuid);

    Page<MallTemplateDo> selectPageUnused(@Param("page") Page<MallTemplateDo> page, @Param("req") MallTemplateQueryRobotListReq req);

    void updateMssIDNullById(@Param("templateIDList") List<String> templateIDList);
}
