package com.citc.nce.auth.certificate;

import cn.hutool.core.bean.BeanUtil;
import com.citc.nce.auth.certificate.entity.UserCertificateDo;
import com.citc.nce.auth.certificate.service.UserCertificateService;
import com.citc.nce.auth.certificate.vo.resp.UserCertificateDownResp;
import com.citc.nce.auth.certificate.vo.resp.UserCertificateResp;
import com.citc.nce.auth.ticket.vo.req.GetCertificateOptionsReq;
import com.citc.nce.auth.user.vo.resp.ListResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class UserCertificateController implements UserCertificateApi {

    @Autowired
    private UserCertificateService userCertificateService;


    /**
     * 获取用户资质（标签）列表
     *
     * @return
     */
    @Override
    @PostMapping("/user/certificate/options")
    public List<UserCertificateResp> getCertificateOptions(GetCertificateOptionsReq req) {
        return userCertificateService.getCertificateOptions(req.getUserId());
    }

    /**
     * 获取用户资质（标签）下拉列表
     */
    @Override
    @GetMapping("/admin/user/certificate/getDownList")
    public List<UserCertificateDownResp> getDownList() {
        List<UserCertificateDo> userCertificateList = userCertificateService.getUserCertificate();
        if (CollectionUtils.isEmpty(userCertificateList)) {
            return null;
        }
        List<UserCertificateDownResp> downResps = BeanUtil.copyToList(userCertificateList, UserCertificateDownResp.class);
        UserCertificateDownResp resp = new UserCertificateDownResp().setId(0L).setCertificateName("全部");
        downResps.add(0, resp);
        return downResps;
    }

    /**
     * 获得用户资质标签，包含用户没有的标签（前端定制化接口）
     *
     * @param req
     * @return
     */
    @Override
    @PostMapping("/user/certificate/getCertificateOptionsAll")
    public List<UserCertificateResp> getCertificateOptionsAll(@RequestBody GetCertificateOptionsReq req) {
        return userCertificateService.getCertificateOptionsAll(req.getUserId());
    }

    @Override
    @PostMapping("/user/getUserCertificateByUserId")
    public ListResp getUserCertificateByUserId(@RequestBody GetCertificateOptionsReq req) {
        return userCertificateService.getUserCertificateByUserId(req);
    }

    @Override
    public Integer getRemark() {

        return userCertificateService.getRemark();
    }
}
