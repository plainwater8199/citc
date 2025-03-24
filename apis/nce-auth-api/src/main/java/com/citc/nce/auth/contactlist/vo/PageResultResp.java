package com.citc.nce.auth.contactlist.vo;

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
    private List<ContactListResp> list;

    private Long total;

    private Integer pageNo;

    public PageResultResp(List<ContactListResp> list, Long total, Integer pageNo) {
        this.list = list;
        this.total = total;
        this.pageNo = pageNo;
    }

    public PageResultResp() {

    }
}
