package com.citc.nce.auth.sharelink;

import com.citc.nce.auth.sharelink.vo.*;
import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.common.facadeserver.annotations.SkipToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @Author: yangchuang
 * @Date: 2022/8/8 15:57
 * @Version: 1.0
 * @Description: 分享链接
 */
@RestController
@Slf4j
@Api(value = "auth", tags = "分享链接")
public class ShareLinkController {
    @Resource
    private ShareLinkApi shareLinkApi;

    /**
     * 分享链接列表分页获取
     *
     * @param
     * @return
     */
    @ApiOperation(value = "分享链接列表分页获取", notes = "分享链接列表分页获取")
    @PostMapping("/share/link/pageList")
    public PageResultResp getShareLinks(@RequestBody @Valid ShareLinkPageReq shareLinkPageReq) {
        return shareLinkApi.getShareLinks(shareLinkPageReq);
    }

    /**
     * 新增分享链接
     *
     * @param
     * @return
     */
    @ApiOperation(value = "新增分享链接", notes = "新增分享链接")
    @PostMapping("/share/link/save")
    public int saveShareLink(@RequestBody  @Valid ShareLinkReq shareLinkReq) {
        return shareLinkApi.saveShareLink(shareLinkReq);
    }

       /**
     * 删除分享链接
     *
     * @param
     * @return
     */
    @ApiOperation(value = "删除分享链接", notes = "删除分享链接")
    @PostMapping("/share/link/delete")
    public int delShareLinkById(@RequestBody  @Valid ShareLinkOneReq shareLinkOneReq) {
        return shareLinkApi.delShareLinkById(shareLinkOneReq);
    }

    /**
     * 根据id获取分享链接
     *
     * @param
     * @return
     */
   /* @ApiOperation(value = "根据id获取分享链接", notes = "根据id获取分享链接")
    @PostMapping("/getOne")
    public ShareLinkResp getShareLinkById(@RequestBody  @Valid ShareLinkOneReq shareLinkOneReq) {
        return shareLinkApi.getShareLinkById(shareLinkOneReq);
    }*/

    /**
     * 校验分享链接
     *
     * @param
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "linkInfo", value = "linkInfo", dataType = "String",required=true)
    })
    @SkipToken
    @ApiOperation(value = "校验分享链接", notes = "校验分享链接")
    @GetMapping("/share/link/checkShareLinkByLinkInfo")
    public ShareLinkH5Resp checkShareLinkByLinkInfo(@RequestParam("linkInfo") String linkInfo) {
        return shareLinkApi.checkShareLinkByLinkInfo(linkInfo);
    }

}
