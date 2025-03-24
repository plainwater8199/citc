package com.citc.nce.filecenter.platform.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年8月19日10:54:57
 * @Version: 1.0
 * @Description: UpReceiveData
 */
@Data
public class UpReceiveData implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer code;

    private String message;

    private FileData data;
}
