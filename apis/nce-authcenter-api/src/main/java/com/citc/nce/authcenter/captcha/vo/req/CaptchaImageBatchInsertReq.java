package com.citc.nce.authcenter.captcha.vo.req;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CaptchaImageBatchInsertReq {

    @NotNull
    @JSONField(serialize = false)
    private List<MultipartFile> images;
}
