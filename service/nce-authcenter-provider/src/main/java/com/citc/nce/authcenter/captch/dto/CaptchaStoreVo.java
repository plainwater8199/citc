package com.citc.nce.authcenter.captch.dto;

import lombok.Data;

import java.util.List;

/**
 * @author bydud
 * @date 2024/11/1
 **/
@Data
public class CaptchaStoreVo {
    private List<String> urls;
}
