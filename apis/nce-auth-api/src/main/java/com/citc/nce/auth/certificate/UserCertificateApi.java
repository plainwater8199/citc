package com.citc.nce.auth.certificate;

import com.citc.nce.auth.certificate.vo.resp.UserCertificateDownResp;
import com.citc.nce.auth.certificate.vo.resp.UserCertificateResp;
import com.citc.nce.auth.ticket.vo.req.GetCertificateOptionsReq;
import com.citc.nce.auth.user.vo.resp.ListResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

@FeignClient(value = "auth-service", contextId = "UserCertificateApi", url = "${auth:}")
public interface UserCertificateApi {


    /**
     * 获取用户资质（标签）列表
     *
     * @return
     */
    @PostMapping("/user/certificate/options")
    List<UserCertificateResp> getCertificateOptions(@RequestBody GetCertificateOptionsReq req);

    @GetMapping("/admin/user/certificate/getDownList")
    List<UserCertificateDownResp> getDownList();

    @PostMapping("/user/certificate/getCertificateOptionsAll")
    List<UserCertificateResp> getCertificateOptionsAll(@RequestBody @Valid GetCertificateOptionsReq req);

    @PostMapping("/user/getUserCertificateByUserId")
    ListResp getUserCertificateByUserId(@RequestBody @Valid GetCertificateOptionsReq req);


    @PostMapping("/admin/user/certificate/getusermark")
    Integer getRemark();
}