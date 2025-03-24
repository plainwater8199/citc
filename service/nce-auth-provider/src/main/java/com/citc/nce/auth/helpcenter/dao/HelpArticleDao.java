package com.citc.nce.auth.helpcenter.dao;

import com.citc.nce.auth.helpcenter.entity.HelpArticleDo;
import com.citc.nce.auth.helpcenter.entity.HelpDirectoryDo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author yy
 * @date 2024-05-11 11:07:29
 */
public interface HelpArticleDao extends BaseMapperX<HelpArticleDo> {
    @Select("select * from help_article where record_type=1 and deleted=0 and doc_id=#{docId} and version=#{version}")
    HelpArticleDo getLastPublishArticle(@Param("docId") String docId, @Param("version") Integer version);
}
