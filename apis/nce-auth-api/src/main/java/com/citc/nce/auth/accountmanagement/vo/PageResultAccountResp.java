package com.citc.nce.auth.accountmanagement.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/7/23 10:00
 * @Version: 1.0
 * @Description:
 */
@Data
public class PageResultAccountResp<T> {
    private List<T> list;

    private Long total;

    private Integer pageNo;

    private String callBack;

    public PageResultAccountResp(List<T> list, Long total, Integer pageNo, String callBack) {
        this.list = list;
        this.total = total;
        this.pageNo = pageNo;
        this.callBack = callBack;
    }


    public PageResultAccountResp() {

    }
}
