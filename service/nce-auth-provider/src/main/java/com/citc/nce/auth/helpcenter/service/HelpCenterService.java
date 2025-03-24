package com.citc.nce.auth.helpcenter.service;

import com.citc.nce.auth.helpcenter.vo.ArticleVo;
import com.citc.nce.auth.helpcenter.vo.DirectoryVo;

/**
 * @author yy
 * @date 2024-05-06 15:02:01
 */

public interface HelpCenterService {

    void publishHelp(DirectoryVo directoryVo);

    void saveDirectory(DirectoryVo directoryVo);

    void saveArticle(ArticleVo articleVo);

    DirectoryVo queryDirectory();

    ArticleVo getArticle(String docId);

    DirectoryVo queryViewDirectory();

    void deleteArticle(String docId);
}
