package com.citc.nce;

import com.citc.nce.auth.csp.mediasms.template.MediaSmsTemplateApi;
import com.citc.nce.auth.csp.mediasms.template.vo.MediaSmsTemplateDetailVo;
import com.citc.nce.tenant.robot.dao.MsgRecordDao;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 内部调用controller 用于数据变更脚本执行
 *
 * @author jcrenc
 * @since 2024/2/28 9:32
 */
@RequiredArgsConstructor
@RestController
public class InternalRobotController {
    @Resource
    private final MsgRecordDao msgRecordDao;
    @Resource
    private final MediaSmsTemplateApi mediaSmsTemplateApi;

    @RequestMapping("test/addConsumeCategoryFiled")
    public void addPayTypeColum() {
        msgRecordDao.addConsumeCategoryFiled();
    }


    @RequestMapping("/media-sms/test")
    public void testMediaSms(@RequestParam String platformTemplateId) {
        MediaSmsTemplateDetailVo template = mediaSmsTemplateApi.getTemplateInfoByPlatformTemplateId(platformTemplateId);
        if (template != null)
            template.setContents(mediaSmsTemplateApi.getContents(template.getId(), true).getContents());
    }

}
