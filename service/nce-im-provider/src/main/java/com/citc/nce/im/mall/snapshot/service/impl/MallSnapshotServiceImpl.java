package com.citc.nce.im.mall.snapshot.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.citc.nce.auth.cardstyle.CardStyleApi;
import com.citc.nce.auth.cardstyle.vo.CardStyleOneReq;
import com.citc.nce.auth.cardstyle.vo.CardStyleResp;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.util.JsonUtils;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.h5.H5Api;
import com.citc.nce.h5.vo.H5Info;
import com.citc.nce.h5.vo.H5InfoListQuery;
import com.citc.nce.im.mall.order.entity.MallRobotOrderDo;
import com.citc.nce.im.mall.order.mapper.MallRobotOrderDao;
import com.citc.nce.im.mall.snapshot.RobotInputUtil;
import com.citc.nce.im.mall.snapshot.entity.MallSnapshotDo;
import com.citc.nce.im.mall.snapshot.entity.ThumbnailResp;
import com.citc.nce.im.mall.snapshot.mapper.MallSnapshotDao;
import com.citc.nce.im.mall.snapshot.model.TsCardStyleContent;
import com.citc.nce.im.mall.snapshot.service.MallSnapshotService;
import com.citc.nce.im.mall.template.service.MallTemplateService;
import com.citc.nce.im.mall.utils.UUIDUtils;
import com.citc.nce.im.mall.variable.entity.MallRobotVariableDo;
import com.citc.nce.im.mall.variable.mapper.MallRobotVariableDao;
import com.citc.nce.im.materialSquare.service.IMsSummaryService;
import com.citc.nce.im.tempStore.service.IResourcesAudioService;
import com.citc.nce.im.tempStore.service.IResourcesFormService;
import com.citc.nce.im.tempStore.service.IResourcesImgService;
import com.citc.nce.im.tempStore.service.IResourcesVideoService;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import com.citc.nce.robot.api.mall.common.*;
import com.citc.nce.robot.api.mall.constant.MallError;
import com.citc.nce.robot.api.mall.snapshot.req.MallRobotOrderSaveOrUpdateReq;
import com.citc.nce.robot.api.mall.snapshot.resp.MallRobotOrderSaveOrUpdateResp;
import com.citc.nce.robot.api.mall.template.vo.resp.MallTemplateQueryDetailResp;
import com.citc.nce.robot.api.materialSquare.emums.MsType;
import com.citc.nce.robot.api.tempStore.domain.ResourcesAudio;
import com.citc.nce.robot.api.tempStore.domain.ResourcesForm;
import com.citc.nce.robot.api.tempStore.domain.ResourcesImg;
import com.citc.nce.robot.api.tempStore.domain.ResourcesVideo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>TODO</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/11/21 17:15
 */
@Service
@Slf4j
public class MallSnapshotServiceImpl implements MallSnapshotService {

    @Resource
    private MallSnapshotDao mallSnapshotDao;
    @Resource
    private IMsSummaryService summaryService;
    @Resource
    @Lazy
    private MallTemplateService mallTemplateService;

    @Resource
    private IResourcesImgService imgService;
    @Resource
    private IResourcesVideoService videoService;
    @Resource
    private IResourcesAudioService audioService;
    @Resource
    private IResourcesFormService formService;
    @Resource
    private MallRobotVariableDao variableDao;
    @Resource
    private MallRobotOrderDao orderDao;
    @Resource
    private CardStyleApi cardStyleApi;
    @Resource
    private H5Api h5Api;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public String dealWithContent(MallCommonContent content, Integer snapshotType) {
        // 因为要支持历史版本，所以每次都是新增数据。
        MallSnapshotDo mallSnapshotDo = new MallSnapshotDo();
        mallSnapshotDo.setSnapshotContent(JsonUtils.obj2String(content));
        mallSnapshotDo.setSnapshotUuid(UUIDUtils.generateUUID());
        mallSnapshotDo.setSnapshotType(snapshotType);

        mallSnapshotDao.insert(mallSnapshotDo);
        return mallSnapshotDo.getSnapshotUuid();
    }

    @Override
    public MallSnapshotDo queryDetail(String snapshotUuid) {
        return baseQuery(snapshotUuid);
    }

    private MallSnapshotDo baseQuery(String snapshotUuid) {
        LambdaQueryWrapperX<MallSnapshotDo> queryWrapperX = new LambdaQueryWrapperX<>();
        queryWrapperX.eq(MallSnapshotDo::getSnapshotUuid, snapshotUuid)
                .eq(MallSnapshotDo::getDeleted, 0);
        return mallSnapshotDao.selectOne(queryWrapperX);
    }

    @Override
    public MallCommonContent queryContent(String snapshotUuid) {
        MallSnapshotDo materialDo = baseQuery(snapshotUuid);
        if (ObjectUtils.isNotEmpty(materialDo) && StringUtils.isNotEmpty(materialDo.getSnapshotContent())) {
            try {
                MallCommonContent resp = JsonUtils.string2Obj(materialDo.getSnapshotContent(), MallCommonContent.class);
                resp.setSnapshotUuid(snapshotUuid);
                return resp;
            } catch (Exception e) {
                throw new BizException(MallError.CONTENT_FORMAT_ERROR);
            }
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MallRobotOrderSaveOrUpdateResp saveOrUpdateSnapshot(MallRobotOrderSaveOrUpdateReq req) {
        MallRobotOrderSaveOrUpdateResp resp = new MallRobotOrderSaveOrUpdateResp();
        String uuid;
        //检查素材是自己创建的
        checkMallCommonContent(req.getMallCommonContent());
        // 首先判定是新增还是更新
        if (StringUtils.isNotEmpty(req.getSnapshotUuid())) {
            uuid = update(req.getTemplateId(), req.getMallCommonContent(), req.getSnapshotType());
        } else {
            uuid = dealWithContent(req.getMallCommonContent(), req.getSnapshotType());
            mallTemplateService.addSnapshotUuid(req.getTemplateId(), uuid);
        }

        //更新商品模板状态
        summaryService.notifyTemplateUpgrade(MsType.ROBOT, req.getTemplateId());
        resp.setSnapshotUuid(uuid);
        return resp;
    }


    /**
     * 为模板商城报错卡片样式快照
     *
     * @param snapshotUuid 快照UUID
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public MallSnapshotDo setCareStyle(String snapshotUuid) {
        if (StrUtil.isBlankIfStr(snapshotUuid)) return null;
        MallSnapshotDo mallSnapshot = baseQuery(snapshotUuid);
        if (Objects.isNull(mallSnapshot)) return mallSnapshot;
        String snapshotContent = mallSnapshot.getSnapshotContent();
        if (StrUtil.isBlankIfStr(snapshotContent)) return mallSnapshot;
        MallCommonContent mallCommonContent = JsonUtils.string2Obj(snapshotContent, MallCommonContent.class);
        if (Objects.isNull(mallCommonContent)) return mallSnapshot;
        if (Objects.isNull(mallCommonContent.getFiveG()) || StrUtil.isBlankIfStr(mallCommonContent.getFiveG().getModuleInformation())) {
            return mallSnapshot;
        }

        String information = mallCommonContent.getFiveG().getModuleInformation();
        JSONObject jsonObject = JSON.parseObject(information);
        JSONObject styleCustom = jsonObject.getJSONObject("styleCustom");
        Long cardStyleId = jsonObject.getLong("style");
        if (Objects.isNull(cardStyleId) || Objects.isNull(styleCustom)) return mallSnapshot;
        CardStyleResp style = cardStyleApi.getCardStyleByIdInner(new CardStyleOneReq().setId(cardStyleId));
        if (Objects.isNull(style)) {
            throw new BizException("卡片样式丢失，请重新设置卡片样式");
        }
        TsCardStyleContent styleContent = new TsCardStyleContent();
        styleContent.setOldCardStyleId(cardStyleId);
        styleContent.setCardStyle(style);
        //clean
        style.setId(null);
        style.setCreator(null);
        style.setCreateTime(null);
        style.setUpdater(null);
        style.setUpdateTime(null);

        JSONObject background = styleCustom.getJSONObject("background");
        if (Objects.nonNull(background)) {
            String req = background.getString("image");
            if (StrUtil.isBlankIfStr(req)) {
                styleContent.setReqUrlId(req);
            }
        }
        //更新数据库
        mallSnapshot.setCardStyleContent(JSON.toJSONString(styleContent));
        mallSnapshotDao.updateById(mallSnapshot);
        return mallSnapshot;
    }

    @Override
    public String getCardStyleContent(String snapshotUuid) {
        return mallSnapshotDao.selectCardStyleContent(snapshotUuid);
    }

    @Override
    public Map<String, String> get5GThumbnailBySnapshotUuid(List<String> snapshotUuids) {
        List<ThumbnailResp> resps = mallSnapshotDao.get5GThumbnailBySnapshotUuid(snapshotUuids);
        if (CollectionUtils.isEmpty(resps)) {
            return null;
        }
        return resps.stream().collect(Collectors.toMap(ThumbnailResp::getSnapshotUuid, ThumbnailResp::getThumbnail));
    }

    /**
     * 检查素材是否都是自己的
     *
     * @param content 需要的素材
     */
    private void checkMallCommonContent(MallCommonContent content) {
        //机器人
        List<Robot> robot = content.getRobot();
        if (CollectionUtils.isNotEmpty(robot)) {
            robot.forEach(this::checkMallContentRobot);
        }
        //5g消息
        checkMallContentFiveG(content.getFiveG());
    }

    //5g消息素材检查
    private void checkMallContentFiveG(FiveG fiveG) {
        if (Objects.isNull(fiveG)) return;
        Material material = fiveG.getMaterial();
        checkMaterial(material);
        checkForm(fiveG.getForm());
    }

    //机器人素材检查
    private void checkMallContentRobot(Robot robot) {
        if (Objects.isNull(robot)) return;
        //普通素材
        checkMaterial(robot.getMaterial());
        //表单
        checkForm(robot.getForm());

        //变量
        List<RobotVariable> variables = robot.getVariables();
        if (CollectionUtils.isNotEmpty(variables)) {
            List<Long> queryId = variables.stream()
                    .filter(s -> RobotInputUtil.type_custom.equalsIgnoreCase(s.getType()))
                    .map(RobotVariable::getId).map(Long::valueOf).distinct()
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(queryId)) {
                List<MallRobotVariableDo> list = variableDao.listByIdsDel(queryId);
                if (list.size() != queryId.size()) {
                    throw new BizException("变量素材缺失");
                }
                list.forEach(s -> SessionContextUtil.sameCsp(s.getCreator()));
            }
        }

        //指令
        List<RobotOrder> orders = robot.getOrders();
        if (CollectionUtils.isNotEmpty(orders)) {
            List<Long> queryId = orders.stream().map(RobotOrder::getId).distinct().collect(Collectors.toList());
            List<MallRobotOrderDo> list = orderDao.listByIdsDel(queryId);
            if (list.size() != queryId.size()) {
                throw new BizException("指令素材缺失");
            }
            list.forEach(s -> SessionContextUtil.sameCsp(s.getCreator()));
        }

    }


    private void checkMaterial(Material material) {
        if (material == null)
            return;
        List<ResourcesImg> pictureList = material.getPictureList();
        if (CollectionUtils.isNotEmpty(pictureList)) {
            Set<Long> queryId = pictureList.stream().map(ResourcesImg::getImgId).collect(Collectors.toSet());
            List<ResourcesImg> imgIds = imgService.listByIdsDel(queryId);
            if (imgIds.size() != queryId.size()) {
                throw new BizException("图片素材缺失");
            }
            imgIds.forEach(s -> SessionContextUtil.sameCsp(s.getCreator()));
        }

        List<ResourcesVideo> videoList = material.getVideoList();
        if (CollectionUtils.isNotEmpty(videoList)) {
            Set<Long> queryId = videoList.stream().map(ResourcesVideo::getVideoId).collect(Collectors.toSet());
            List<ResourcesVideo> videoIds = videoService.listByIdsDel(queryId);
            if (videoIds.size() != queryId.size()) {
                throw new BizException("视频素材缺失");
            }
            videoIds.forEach(s -> SessionContextUtil.sameCsp(s.getCreator()));
        }

        List<ResourcesAudio> audioList = material.getAudioList();
        if (CollectionUtils.isNotEmpty(audioList)) {
            Set<Long> queryId = audioList.stream().map(ResourcesAudio::getAudioId).collect(Collectors.toSet());
            List<ResourcesAudio> audioIds = audioService.listByIdsDel(queryId);
            if (audioIds.size() != queryId.size()) {
                throw new BizException("音频素材缺失");
            }
            audioIds.forEach(s -> SessionContextUtil.sameCsp(s.getCreator()));
        }
    }

    private void checkForm(List<Form> form) {
        if (CollectionUtils.isNotEmpty(form)) {
            Set<Long> queryId = form.stream().map(Form::getId).collect(Collectors.toSet());
            H5InfoListQuery query = new H5InfoListQuery();
            query.setIds(new ArrayList<>(queryId));
            List<H5Info> fromIds = h5Api.byIds(query);
            if (log.isDebugEnabled()) {
                log.debug("H5表单校验 query:{}  fromIds: {}", query, fromIds);
            }
            if (fromIds.size() != queryId.size()) {
                throw new BizException("表单素材缺失");
            }
//            fromIds.forEach(s -> SessionContextUtil.sameCsp(s.get()));
        }
    }


    private String update(String templateId, MallCommonContent mallCommonContent, Integer snapshotType) {
        MallTemplateQueryDetailResp templateQueryDetailResp = mallTemplateService.queryDetail(templateId);
        MallSnapshotDo mallSnapshotDo = baseQuery(templateQueryDetailResp.getSnapshotUuid());
        if (ObjectUtils.isEmpty(mallSnapshotDo)) {
            throw new BizException(MallError.SNAPSHOT_NOT_EXIST);
        }
        SessionContextUtil.sameCsp(mallSnapshotDo.getCreator());
        mallSnapshotDo.setSnapshotContent(JsonUtils.obj2String(mallCommonContent));
        mallSnapshotDao.updateById(mallSnapshotDo);
        return mallSnapshotDo.getSnapshotUuid();
    }

}
