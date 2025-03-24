package com.citc.nce.auth.cardstyle.vo;

import com.citc.nce.auth.contactlist.vo.ContactListResp;
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
    private List<CardStyleResp> list;

    private Long total;

    private Integer pageNo;

    public PageResultResp(List<CardStyleResp> list, Long total, Integer pageNo) {
        this.list = list;
        this.total = total;
        this.pageNo = pageNo;
    }

    public PageResultResp() {

    }
}
