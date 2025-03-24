package com.citc.nce.auth.helpcenter;

import com.citc.nce.auth.helpcenter.service.HelpCenterService;
import com.citc.nce.auth.helpcenter.vo.ArticleVo;
import com.citc.nce.auth.helpcenter.vo.DirectoryVo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author yy
 * @date 2024-05-06 09:50:30
 * 帮助中心
 */
@RestController()
@Slf4j
public class HelpCenterController implements HelpCenterEditApi, HelpCenterViewApi {
    @Resource
    private HelpCenterService helpCenterService;

    @PostMapping("/help/edit/publish")
    @ApiOperation(value = "帮助中心发布", notes = "帮助中心发布")
    @Override
    public void publishHelp(@RequestBody DirectoryVo directoryVo) {
        helpCenterService.publishHelp(directoryVo);
    }

    @PostMapping("/help/edit/saveDirectory")
    @ApiOperation(value = "保存目录", notes = "保存目录")
    @Override
    public void saveDirectory(@RequestBody DirectoryVo directoryVo) {
        helpCenterService.saveDirectory(directoryVo);
    }

    @PostMapping("/help/edit/saveArticle")
    @ApiOperation(value = "保存帮助中心文档", notes = "保存帮助中心文档")
    @Override
    public void saveArticle(@RequestBody ArticleVo articleVo) {
         helpCenterService.saveArticle(articleVo);
    }

    @GetMapping("/help/edit/getDirectory")
    @ApiOperation(value = "后管获取目录", notes = "后管获取目录")
    @Override
    public DirectoryVo getDirectory() {
        return helpCenterService.queryDirectory();
    }

    @GetMapping("/help/edit/getArticle")
    @ApiOperation(value = "后管获取帮助中心文档", notes = "后管获取帮助中心文档")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "文档id", value = "docId", dataType = "String", required = true)
    })
    @Override
    public ArticleVo getArticle(@RequestParam("docId") String docId) {
        return helpCenterService.getArticle(docId);
    }

    @GetMapping("/help/edit/deleteArticle")
    @ApiOperation(value = "后管删除帮助中心文档", notes = "后管获取帮助中心文档")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "文档id", value = "docId", dataType = "String", required = true)
    })
    @Override
    public void deleteArticle(@RequestParam("docId") String docId) {
        helpCenterService.deleteArticle(docId);
    }

    @GetMapping("/help/view/getDirectory")
    @ApiOperation(value = "前端浏览获取目录", notes = "前端浏览获取目录")
    @Override
    public DirectoryVo getViewDirectory() {
        return helpCenterService.queryViewDirectory();
    }

}
