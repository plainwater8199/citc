package com.citc.nce.im.massSegment.service;

import com.citc.nce.auth.constant.csp.common.CSPOperatorCodeEnum;
import com.citc.nce.im.massSegment.entity.MassSegment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.robot.domain.massSegment.MassSegmentDetail;
import com.citc.nce.robot.domain.massSegment.MassSegmentVo;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 运营商号段关系表 服务类
 * </p>
 *
 * @author bydud
 * @since 2024-05-06 04:05:59
 */
public interface IMassSegmentService extends IService<MassSegment> {
    String CUSTOM = "custom";

    /**
     * 根据手机号前缀查询运营商
     *
     * @param phone 手机号/前3位
     * @return
     */
    String getOperatorStringByPhone(String phone);

    CSPOperatorCodeEnum getOperatorByPhone(String phone);

    Map<String, CSPOperatorCodeEnum> getOperatorByPhone(List<String> phones);

    /**
     保存自定义号段
     */
    void saveCustom(MassSegmentVo entity);

    /**
     删除自定义号段
     */
    void delById(Long id);

    /**
     查询全部自定义号段
     */
    Map<String, List<MassSegmentDetail>> listGroupOperator(String msType);

    default MassSegmentDetail changeToMassSegmentDetail(@NotNull MassSegment massSegment) {
        MassSegmentDetail detail = new MassSegmentDetail();
        detail.setId(massSegment.getId());
        detail.setOperator(massSegment.getOperator());
        detail.setPhoneSegment(massSegment.getPhoneSegment());
        detail.setMsType(massSegment.getMsType());
        return detail;
    }

    Map<String, String> queryAllSegment();
}
