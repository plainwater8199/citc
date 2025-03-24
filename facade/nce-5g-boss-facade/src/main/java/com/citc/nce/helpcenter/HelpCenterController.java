package com.citc.nce.helpcenter;

import com.citc.nce.annotation.BossAuth;
import com.citc.nce.auth.helpcenter.HelpCenterEditApi;
import com.citc.nce.auth.helpcenter.vo.ArticleVo;
import com.citc.nce.auth.helpcenter.vo.DirectoryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author yy
 * @date 2024-05-11 16:53:21
 */
@Api(tags = "后台管理-帮助中心")
@RestController
public class HelpCenterController {
    @Resource
    HelpCenterEditApi helpCenterEditApi;
    @BossAuth("/chatbot-view/help-center")
    @PostMapping("/help/edit/publish")
    @ApiOperation(value = "帮助中心发布", notes = "帮助中心发布")
   
    public void publishHelp(@RequestBody DirectoryVo directoryVo) {
        helpCenterEditApi.publishHelp(directoryVo);
    }

    @BossAuth("/chatbot-view/help-center")
    @PostMapping("/help/edit/saveDirectory")
    @ApiOperation(value = "保存目录", notes = "保存目录")
    public void saveDirectory(@RequestBody DirectoryVo directoryVo) {
        helpCenterEditApi.saveDirectory(directoryVo);
    }

    @BossAuth({"/chatbot-view/help-center/editDoc","/chatbot-view/help-center/previewDoc"})
    @PostMapping("/help/edit/saveArticle")
    @ApiOperation(value = "保存帮助中心文档", notes = "保存帮助中心文档")
    public void saveArticle(@RequestBody ArticleVo articleVo) {
        helpCenterEditApi.saveArticle(articleVo);
    }

    @BossAuth({"/chatbot-view/help-center","/chatbot-view/help-center/previewDoc"})
    @GetMapping("/help/edit/getDirectory")
    @ApiOperation(value = "后管获取目录", notes = "后管获取目录")
    public DirectoryVo getDirectory() {
        return helpCenterEditApi.getDirectory();
    }


    @BossAuth("/chatbot-view/help-center/editDoc")
    @GetMapping("/help/edit/getArticle")
    @ApiOperation(value = "后管获取帮助中心文档", notes = "后管获取帮助中心文档")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "文档id", value = "docId", dataType = "String", required = true)
//    })
    public ArticleVo getArticle(@RequestParam("docId") String docId) {
        return helpCenterEditApi.getArticle(docId);
    }

    @BossAuth("/chatbot-view/help-center")
    @GetMapping("/help/edit/deleteArticle")
    @ApiOperation(value = "后管删除帮助中心文档", notes = "后管获取帮助中心文档")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "文档id", value = "docId", dataType = "String", required = true)
//    })
    public void deleteArticle(@RequestParam("docId") String docId) {
        helpCenterEditApi.deleteArticle(docId);
    }
    
}
