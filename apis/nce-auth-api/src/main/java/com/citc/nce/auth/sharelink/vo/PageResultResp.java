package com.citc.nce.auth.sharelink.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/7/23 10:00
 * @Version: 1.0
 * @Description:
 */
@Data
public class PageResultResp implements Serializable {
    private List<ShareLinkResp> list;

    private Long total;

    private Integer pageNo;

    private String shareLink;
    public PageResultResp(List<ShareLinkResp> list, Long total, Integer pageNo,String shareLink) {
        this.list = list;
        this.total = total;
        this.pageNo = pageNo;
        this.shareLink = shareLink;
    }

    public PageResultResp() {

    }
}
