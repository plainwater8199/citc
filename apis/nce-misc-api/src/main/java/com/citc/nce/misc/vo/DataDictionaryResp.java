package com.citc.nce.misc.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022/6/28 16:04
 * @Version: 1.0
 * @Description:
 */
@Data
public class DataDictionaryResp {

    private Long id;

    private String content;

    private Long parentId;

    List<DataDictionaryResp> children;
}
