package com.citc.nce.im.materialSquare.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.util.JsonUtils;
import com.citc.nce.customcommand.CustomCommandApi;
import com.citc.nce.customcommand.vo.CustomCommandDetailVo;
import com.citc.nce.h5.H5Api;
import com.citc.nce.h5.vo.H5Info;
import com.citc.nce.im.mall.snapshot.entity.MallSnapshotDo;
import com.citc.nce.im.mall.snapshot.service.MallSnapshotService;
import com.citc.nce.im.mall.template.service.MallTemplateService;
import com.citc.nce.im.materialSquare.mapper.MsSummaryContentMapper;
import com.citc.nce.im.materialSquare.service.IMsSummaryContentService;
import com.citc.nce.modulemanagement.ModuleManagementApi;
import com.citc.nce.robot.api.mall.common.MallCommonContent;
import com.citc.nce.robot.api.mall.constant.MallError;
import com.citc.nce.robot.api.materialSquare.entity.MsSummary;
import com.citc.nce.robot.api.materialSquare.entity.MsSummaryContent;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * <p>
 * 素材广场，发布汇总，作品内容快照 服务实现类
 * </p>
 *
 * @author bydud
 * @since 2024-05-31 09:05:58
 */
@Service
@AllArgsConstructor
public class MsSummaryContentServiceImpl extends ServiceImpl<MsSummaryContentMapper, MsSummaryContent> implements IMsSummaryContentService {


    private final MallTemplateService mallTemplateService;
    private final MallSnapshotService mallSnapshotService;
    private final CustomCommandApi commandApi;
    private final ModuleManagementApi managementApi;
    private final H5Api h5Api;

    @Override
    public MsSummaryContent getByVersion(Long mssId, Integer contentVersion) {
        return lambdaQuery()
                .eq(MsSummaryContent::getMssId, mssId)
                .eq(MsSummaryContent::getContentVersion, contentVersion)
                .one();
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Integer saveContent(MsSummary summary) {
        MsSummaryContent last = getLastMsSummary(summary.getMssId());
        int version = Objects.isNull(last) ? 1 : last.getContentVersion() + 1;
        MsSummaryContent content = new MsSummaryContent();
        content.setMssId(summary.getMssId());
        content.setContentVersion(version);
        content.setMsType(summary.getMsType());

        String msJson;
        String msId = summary.getMsId();
        switch (summary.getMsType()) {
            case ROBOT:
                msJson = saveRobotContent(msId);
                break;
            case NR_SSG:
                msJson = saveNRContent(msId);
                break;
            case H5_FORM:
                msJson = saveH5FormContent(msId);
                break;
            case CUSTOM_ORDER:
                msJson = saveCustomOrderContent(msId);
                break;
            case SYSTEM_MODULE:
                msJson = saveSystemModuleContent(msId);
                break;
            default:
                throw new RuntimeException("不支持的素材类型");
        }

        content.setMsJson(msJson);
        save(content);
        return version;
    }

    private String saveH5FormContent(String msId) {
        H5Info detail = h5Api.getDetailForSummery(Long.valueOf(msId));
        if (ObjectUtils.isEmpty(detail)) {
            throw new BizException(MallError.TEMPLATE_NOT_EXIST);
        }
        return JSON.toJSONString(detail);
    }

    private String saveSystemModuleContent(String msId) {
        return JSON.toJSONString(managementApi.queryById(Long.valueOf(msId)));
    }

    @Override
    public MsSummaryContent getLastMsSummary(Long mssId) {
        return lambdaQuery()
                .eq(MsSummaryContent::getMssId, mssId)
                .orderByDesc(MsSummaryContent::getContentVersion)
                .last("limit 1")
                .one();
    }

    //机器人素材
    private String saveRobotContent(String templateId) {
        MallCommonContent mallCommonContent = mallTemplateService.getMallCommonContent(templateId);
        if (mallCommonContent == null) {
            return "{}";
        }
        return JSON.toJSONString(mallCommonContent);
    }

    //5g消息素材
    private String saveNRContent(String templateId) {
        MallCommonContent mallCommonContent = mallTemplateService.getMallCommonContent(templateId);
        if(mallCommonContent == null) {
            return "{}";
        }
        MallSnapshotDo snapshotDo = mallSnapshotService.setCareStyle(mallCommonContent.getSnapshotUuid());
        MallCommonContent resp = JsonUtils.string2Obj(snapshotDo.getSnapshotContent(), MallCommonContent.class);
        resp.setSnapshotUuid(mallCommonContent.getSnapshotUuid());
        resp.setCreateTime(mallCommonContent.getCreateTime());
        resp.setUpdateTime(mallCommonContent.getUpdateTime());
        return JSON.toJSONString(resp);
    }

    //自定义指令素材
    private String saveCustomOrderContent(String templateId) {
        CustomCommandDetailVo detailVo = commandApi.getById(templateId);
        return JSON.toJSONString(detailVo);
    }

}
