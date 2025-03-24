package com.citc.nce.auth.helpcenter;

import com.citc.nce.auth.helpcenter.vo.ArticleVo;
import com.citc.nce.auth.helpcenter.vo.DirectoryVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author yy
 * @date 2024-05-06 09:52:18
 * @Description:帮助中心后管编辑
 */
@FeignClient(value = "auth-service", contextId = "HelpCenterEdit", url = "${auth:}")

public interface HelpCenterEditApi {
   @PostMapping("/help/edit/publish")
   void publishHelp(@RequestBody DirectoryVo directoryVo);
   @PostMapping("/help/edit/saveDirectory")
   void saveDirectory(@RequestBody DirectoryVo directoryVo);
   @PostMapping("/help/edit/saveArticle")
   void saveArticle(@RequestBody @Validated ArticleVo articleVo);
   @GetMapping("/help/edit/getDirectory")
   DirectoryVo getDirectory();
   @GetMapping("/help/edit/getArticle")
   ArticleVo getArticle(@RequestParam("docId") String docId);
   @GetMapping("/help/edit/deleteArticle")
   void deleteArticle(@RequestParam("docId")String docId);

}
