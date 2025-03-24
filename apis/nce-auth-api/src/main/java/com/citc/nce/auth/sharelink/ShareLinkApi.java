package com.citc.nce.auth.sharelink;

import com.citc.nce.auth.sharelink.vo.*;
import com.citc.nce.common.core.pojo.PageParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/8/9 16:53
 * @Version: 1.0
 * @Description:分享链接
 */

@FeignClient(value = "auth-service", contextId = "ShareLinkApi", url = "${auth:}")
public interface ShareLinkApi {
    /**
     * 分享链接列表分页获取
     *
     * @param
     * @return
     */
    @PostMapping("/share/link/pageList")
    PageResultResp getShareLinks(@RequestBody @Valid ShareLinkPageReq shareLinkPageReq);

    /**
     * 新增分享链接
     *
     * @param
     * @return
     */
    @PostMapping("/share/link/save")
    int saveShareLink(@RequestBody @Valid ShareLinkReq ShareLinkReq);

       /**
     * 删除分享链接
     *
     * @param
     * @return
     */
    @PostMapping("/share/link/delete")
    int delShareLinkById(@RequestBody @Valid ShareLinkOneReq ShareLinkOneReq);

    /**
     * 获取单个分享链接
     *
     * @param
     * @return
     */
   /* @PostMapping("/share/link/getOne")
    ShareLinkResp getShareLinkById(@RequestBody @Valid ShareLinkOneReq ShareLinkOneReq);*/


    @GetMapping("/share/link/checkShareLinkByLinkInfo")
    ShareLinkH5Resp checkShareLinkByLinkInfo(@RequestParam("linkInfo") String linkInfo);

 }
