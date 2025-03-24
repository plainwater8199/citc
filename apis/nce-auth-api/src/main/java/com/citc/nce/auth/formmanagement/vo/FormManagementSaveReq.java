package com.citc.nce.auth.formmanagement.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/8/9 17:03
 * @Version: 1.0
 * @Description:
 */
@Data
public class FormManagementSaveReq implements Serializable {

    List<FormManagementReq> list;
}
