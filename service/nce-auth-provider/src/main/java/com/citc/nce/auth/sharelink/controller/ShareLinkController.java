package com.citc.nce.auth.sharelink.controller;

import com.citc.nce.auth.sharelink.ShareLinkApi;
import com.citc.nce.auth.sharelink.service.ShareLinkService;
import com.citc.nce.auth.sharelink.vo.*;
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
@RestController()
@Slf4j
public class ShareLinkController implements ShareLinkApi {
    @Resource
    private ShareLinkService shareLinkService;

    /**
     * 分享链接列表分页获取
     *
     * @param
     * @return
     */
    @ApiOperation(value = "分享链接列表分页获取", notes = "分享链接列表分页获取")
    @PostMapping("/share/link/pageList")
    @Override
    public PageResultResp getShareLinks(@RequestBody @Valid ShareLinkPageReq shareLinkPageReq) {
        return shareLinkService.getShareLinks(shareLinkPageReq);
    }

    /**
     * 新增分享链接
     *
     * @param
     * @return
     */
    @ApiOperation(value = "新增分享链接", notes = "新增分享链接")
    @PostMapping("/share/link/save")
    @Override
    public int saveShareLink(@RequestBody  @Valid ShareLinkReq shareLinkReq) {
        return shareLinkService.saveShareLink(shareLinkReq);
    }

       /**
     * 删除分享链接
     *
     * @param
     * @return
     */
    @ApiOperation(value = "删除分享链接", notes = "删除分享链接")
    @PostMapping("/share/link/delete")
    @Override
    public int delShareLinkById(@RequestBody  @Valid ShareLinkOneReq shareLinkOneReq) {
        return shareLinkService.delShareLinkById(shareLinkOneReq);
    }

    /**
     * 根据id获取分享链接
     *
     * @param
     * @return
     */
    /*@ApiOperation(value = "根据id获取分享链接", notes = "根据id获取分享链接")
    @PostMapping("/share/link/getOne")
    @Override
    public ShareLinkResp getShareLinkById(@RequestBody  @Valid ShareLinkOneReq shareLinkOneReq) {
        return shareLinkService.getShareLinkById(shareLinkOneReq);
    }*/

    /**
     * 校验分享链接
     *
     * @param
     * @return
     */
    @ApiOperation(value = "校验分享链接", notes = "校验分享链接")
    @GetMapping("/share/link/checkShareLinkByLinkInfo")
    @Override
    public ShareLinkH5Resp checkShareLinkByLinkInfo(@RequestParam("linkInfo") String linkInfo) {
        return shareLinkService.checkShareLinkByLinkInfo(linkInfo);
    }

}
