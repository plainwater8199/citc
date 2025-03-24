package com.citc.nce.auth.sharelink.service;

import com.citc.nce.auth.sharelink.vo.*;
import com.citc.nce.common.core.pojo.PageParam;

import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/8/15 16:30
 * @Version: 1.0
 * @Description:
 */
public interface ShareLinkService {


    PageResultResp getShareLinks(ShareLinkPageReq shareLinkPageReq);

    int saveShareLink(ShareLinkReq shareLinkReq);

    int delShareLinkById(ShareLinkOneReq shareLinkOneReq);

    ShareLinkResp getShareLinkById(ShareLinkOneReq shareLinkOneReq);

    ShareLinkH5Resp checkShareLinkByLinkInfo(String linkInfo);
}
