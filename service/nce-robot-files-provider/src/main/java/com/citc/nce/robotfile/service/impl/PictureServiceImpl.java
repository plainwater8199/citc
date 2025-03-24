package com.citc.nce.robotfile.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.common.vo.tempStore.Csp4CustomerImg;
import com.citc.nce.dto.CustomCommandPictureReq;
import com.citc.nce.dto.MoveGroupReq;
import com.citc.nce.dto.PageReq;
import com.citc.nce.dto.PictureReq;
import com.citc.nce.filecenter.FileApi;
import com.citc.nce.filecenter.vo.DeleteReq;
import com.citc.nce.materialSquare.entity.MsManageActivity;
import com.citc.nce.robotfile.configure.SuperAdministratorUserIdConfigure;
import com.citc.nce.robotfile.entity.PictureDo;
import com.citc.nce.robotfile.exp.RobotFileExp;
import com.citc.nce.robotfile.mapper.GroupMapper;
import com.citc.nce.robotfile.mapper.PictureMapper;
import com.citc.nce.robotfile.service.IExamineResultService;
import com.citc.nce.robotfile.service.IPictureService;
import com.citc.nce.vo.*;
import com.citc.nce.vo.tempStore.UpdateTid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年6月29日16:29:48
 * @Version: 1.0
 * @Description: PictureServiceImpl
 */
@Service
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(SuperAdministratorUserIdConfigure.class)
public class PictureServiceImpl extends ServiceImpl<PictureMapper, PictureDo> implements IPictureService {

    @Autowired
    private PictureMapper pictureMapper;

    @Resource
    private IExamineResultService examineResultService;

    @Resource
    private AccountManagementApi accountManagementApi;

    @Resource
    private GroupMapper groupMapper;

    private final SuperAdministratorUserIdConfigure superAdministratorUserIdConfigure;

    @Resource
    private FileApi fileApi;

    @Override
    public PageResultResp<PictureResp> selectAll(PageReq req) {
        if (req.getPageNo() == 0 || req.getPageSize() == 0) {
            req.setPageSize(5);
            req.setPageNo(1);
        }
        return selectByPage(req);
    }

    @Override
    @Transactional
    public void customCommandSave(CustomCommandPictureReq dto) {
        //检查是否有重复的
        QueryWrapper<PictureDo> wrapper = new QueryWrapper<>();
        wrapper.eq("picture_url_id", dto.getPictureUrlId());
        PictureDo exit = pictureMapper.selectOne(wrapper);
        if (exit == null) {
            PictureDo picture = new PictureDo();
            picture.setPictureUrlId(dto.getPictureUrlId());
            picture.setPictureName(dto.getPictureName());
            picture.setPictureFormat(dto.getPictureFormat());
            picture.setPictureSize(dto.getPictureSize());
            picture.setPictureUploadTime(new Date());
            if (StringUtils.isNotEmpty(dto.getThumbnailTid())) {
                picture.setThumbnailTid(dto.getThumbnailTid());
            }
            if (null != dto.getGroupId()) {
                picture.setGroupId(dto.getGroupId());
            }
            if (StringUtils.isNotEmpty(dto.getCreator())) {
                picture.setCreator(dto.getCreator());
            }
            save(picture);
        }
    }

    @Override
    @Transactional
    public void savePicture(PictureReq dto) {
        //检查是否有重复的

        QueryWrapper<PictureDo> wrapper = new QueryWrapper<>();
        wrapper.eq("picture_url_id", dto.getPictureUrlId());
        PictureDo exit = pictureMapper.selectOne(wrapper);
        if (exit == null) {
            PictureDo picture = new PictureDo();
            picture.setPictureUrlId(dto.getPictureUrlId());
            picture.setPictureName(dto.getPictureName());
            picture.setPictureFormat(dto.getPictureFormat());
            picture.setPictureSize(dto.getPictureSize());
            picture.setPictureUploadTime(new Date());
            if (StringUtils.isNotEmpty(dto.getThumbnailTid())) {
                picture.setThumbnailTid(dto.getThumbnailTid());
            }
            if (null != dto.getGroupId()) {
                picture.setGroupId(dto.getGroupId());
            }
            save(picture);
            //将数据保存至审核数据库
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePictureList(List<PictureReq> req) {
        if (CollectionUtils.isEmpty(req)) {
            return;
        }
        List<String> urlIdList = new ArrayList<>();
        HashMap<String, PictureDo> insertMap = new HashMap<>();
        for (PictureReq pictureReq :
                req) {
            urlIdList.add(pictureReq.getPictureUrlId());
            PictureDo picture = new PictureDo();
            picture.setPictureUrlId(pictureReq.getPictureUrlId());
            picture.setPictureName(pictureReq.getPictureName());
            picture.setPictureFormat(pictureReq.getPictureFormat());
            picture.setPictureSize(pictureReq.getPictureSize());
            picture.setPictureUploadTime(new Date());
            picture.setDeleted(0);
            picture.setDeleteTime(new Date());
            picture.setUsed(0);
            if (StringUtils.isNotEmpty(pictureReq.getThumbnailTid())) {
                picture.setThumbnailTid(pictureReq.getThumbnailTid());
            }
            if (null != pictureReq.getGroupId()) {
                picture.setGroupId(pictureReq.getGroupId());
            }
            insertMap.put(pictureReq.getPictureUrlId(), picture);
        }
        // 检查是否有重复的
        try {
            QueryWrapper<PictureDo> wrapper = new QueryWrapper<>();
            wrapper.in("picture_url_id", urlIdList);
            List<PictureDo> pictureDos = pictureMapper.selectList(wrapper);
            // 去重
            if (CollectionUtils.isNotEmpty(pictureDos)) {
                pictureDos.stream().forEach(pictureDo -> insertMap.remove(pictureDo.getPictureUrlId()));
            }
            Iterator<Map.Entry<String, PictureDo>> iterator = insertMap.entrySet().iterator();
            List<PictureDo> insertList = new ArrayList<>();
            while (iterator.hasNext()) {
                insertList.add(iterator.next().getValue());
            }
            if (CollectionUtils.isNotEmpty(insertList)) {
                pictureMapper.insertBatch(insertList);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
//            throw new BizException(RobotFileExp.SQL_ERROR);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<Long, Csp4CustomerImg> savePictureListCsp4CustomerImg(List<Csp4CustomerImg> list) {
        if (CollectionUtils.isEmpty(list)) {
            return new HashMap<>();
        }
        //用户收集新id returnMap <老id,数据>
        Map<Long, Csp4CustomerImg> returnMap = list.stream()
                .collect(Collectors.toMap(Csp4CustomerImg::getImgId, Function.identity()));

        Map<String, PictureDo> insertMap = list.stream().map(s -> {
            PictureDo picture = new PictureDo();
            picture.setPictureUrlId(s.getPictureUrlid());
            picture.setPictureName(s.getPictureName());
            picture.setPictureFormat(s.getPictureFormat());
            picture.setPictureSize(s.getPictureSize());
            picture.setPictureUploadTime(new Date());
            picture.setDeleted(0);
            picture.setDeleteTime(new Date());
            picture.setUsed(0);
            if (StringUtils.isNotEmpty(s.getThumbnailTid())) {
                picture.setThumbnailTid(s.getThumbnailTid());
            }
            picture.setGroupId(0L);
            //备份原始id
            picture.setOldId(s.getImgId());
            return picture;
        }).collect(Collectors.toMap(PictureDo::getPictureUrlId, Function.identity()));


        List<PictureDo> insertList = new ArrayList<>(insertMap.values());
        if (CollectionUtils.isNotEmpty(insertList)) {
            insertList.forEach(s -> pictureMapper.insert(s));
        }

        //回写新id;
        for (PictureDo pic : insertList) {
            Csp4CustomerImg entity = returnMap.get(pic.getOldId());
            if (Objects.isNull(entity)) {
                log.warn("Csp4CustomerImg lost file , oldId: {}", pic.getOldId());
                continue;
            }
            log.info("pic {}", pic);
            entity.setNewId(pic.getId());
        }
        return returnMap;
    }

    @Override
    @Transactional
    public void updateTid(List<UpdateTid> updateTidList) {
        for (UpdateTid updateTid : updateTidList) {
            update(new LambdaUpdateWrapper<PictureDo>()
                    //图片没缩略图，用的主文件文件的tid
                    .set(PictureDo::getThumbnailTid, updateTid.getUploadResp().getFileTid())
                    .eq(PictureDo::getId, updateTid.getId()));
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updatePictureThumbnail(String pictureUrlId, String thumbnailId) {
        lambdaUpdate()
                .eq(PictureDo::getPictureUrlId, pictureUrlId)
                .set(PictureDo::getThumbnailTid, thumbnailId)
                .update();
    }


    @Override
    public void movePicture(MoveGroupReq req) {
        if (CollectionUtil.isEmpty(req.getPictureIds()) || req.getGroupId() == 0) {
            throw new BizException(RobotFileExp.SQL_ERROR);
        }

        String userId = SessionContextUtil.getUser().getUserId();
        List<PictureDo> pictureDos = pictureMapper.selectBatchIds(req.getPictureIds());
        if (!pictureDos.stream().allMatch(s -> s.getCreator().equals(userId))) {
            throw new BizException("不能移动别人的资源");
        }

        for (Long pictureId : req.getPictureIds()) {
            UpdateWrapper<PictureDo> wrapper = new UpdateWrapper<>();
            wrapper.eq("id", pictureId);
            wrapper.set("group_id", req.getGroupId());
            boolean result = update(wrapper);
            if (!result) {
                throw new BizException(RobotFileExp.SQL_ERROR);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeleteResp deletePicture(MoveGroupReq req) {
        if (CollectionUtil.isEmpty(req.getPictureIds())) {
            throw new BizException(RobotFileExp.BAD_REQUEST);
        }
        DeleteResp resp = new DeleteResp();
        List<PictureDo> pictureDos = pictureMapper.selectBatchIds(req.getPictureIds());
        if (CollectionUtil.isEmpty(pictureDos)) return resp;
        String userId = SessionContextUtil.getUser().getUserId();
        if (!pictureDos.stream().allMatch(s -> s.getCreator().equals(userId))) {
            throw new BizException("不能删除别人的资源");
        }

        QueryWrapper<PictureDo> wrapper = new QueryWrapper<>();
        wrapper.in("id", req.getPictureIds());
        List<PictureDo> pictures = pictureMapper.selectList(wrapper);
        if (req.getDeleted() == 0) {
            pictures.forEach(pictureDo -> {
                pictureDo.setGroupId(0L);
            });
            pictureMapper.updateBatch(pictures);
            return resp;
        }
        List<String> list = new ArrayList<>();
        pictures.forEach(pictureDo -> {
            list.add(pictureDo.getPictureUrlId());
        });
        List<Long> ids = pictures.stream().map(PictureDo::getId).collect(Collectors.toList());
        int count = pictureMapper.logicDeleteByIds(ids);
        if (count <= 0) {
            throw new BizException(RobotFileExp.SQL_ERROR);
        }
        resp.setFileUrlIds(list);
        DeleteReq deleteReq = new DeleteReq();
        deleteReq.setFileUrlIds(list);
        fileApi.deleteFile(deleteReq);
        return resp;
    }


    public PageResultResp<PictureResp> selectByPage(PageReq pageReq) {
        log.info("查询素材参数：{}", JSONObject.toJSONString(pageReq));
        PageResultResp<PictureResp> pageResult = new PageResultResp<>();
        QueryWrapper<PictureDo> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", 0);
        wrapper.orderByDesc("picture_upload_time");
        if (null != pageReq.getId()) {
            wrapper.eq("group_id", pageReq.getId());
        }
        if (!StringUtils.isEmpty(pageReq.getTabs())) {
            if (!CollectionUtil.isEmpty(pageReq.getMaterialIds())) {
                wrapper.in("id", pageReq.getMaterialIds());
            } else {
                pageResult.setTotal(0L);
                pageResult.setList(new ArrayList<>());
                return pageResult;
            }
        }
        String userId = SessionContextUtil.getUser().getUserId();
        if (!StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
            wrapper.eq("creator", userId);
        }
        List<PictureDo> records = pictureMapper.selectList(wrapper);
        log.info("素材总记录数：{}", records.size());
        List<PictureResp> vos = getPictureResps(pageReq, records, userId);
        log.info("素材总记录数：{}", vos.size());
        List<PictureResp> result = filterResult(vos, pageReq);
        log.info("筛选状态后数量：{}", result.size());
        result = filterAllAccount(result, pageReq);
        log.info("filterAllAccount后数量：{}", result.size());
        List<PictureResp> newResult = result.stream().skip((long) (pageReq.getPageNo() - 1) * pageReq.getPageSize())
                .limit(pageReq.getPageSize()).collect(Collectors.toList());
        pageResult.setTotal((long) result.size());
        if (result.size() == 0) {
            pageResult.setTotal(0L);
        }
        List<String> fileUrls = newResult.stream().map(PictureResp::getPictureUrlId).distinct().collect(Collectors.toList());
        Map<String, String> autoThumbnail = fileApi.getAutoThumbnail(fileUrls);
        for(PictureResp pictureResp : newResult){
            pictureResp.setAutoThumbnail(autoThumbnail.get(pictureResp.getPictureUrlId()));
        }
        pageResult.setList(newResult);
        return pageResult;
    }

    private List<PictureResp> filterResult(List<PictureResp> list, PageReq pageReq) {
        //筛选文件名
        List<PictureResp> fileNameList = filterFileName(list, pageReq.getFileName());
        log.info("筛选文件名后数量：{}", fileNameList.size());
        //筛选账号
        List<PictureResp> accountList = filterAccount(fileNameList, pageReq.getChatbotName(), pageReq.getChatbotAccounts());
        log.info("筛选账号后数量：{}", fileNameList.size());
        //筛选状态
        return filterStatus(accountList, pageReq);
    }

    private List<PictureResp> filterStatus(List<PictureResp> accountList, PageReq pageReq) {
        List<PictureResp> result = new ArrayList<>();
        if (null == pageReq.getStatus() || pageReq.getStatus() == 0) {
            return accountList;
        }
        for (PictureResp pictureResp : accountList) {
            // 查询状态的同时，如果chatbotName不为空，需要过滤
            List<AccountResp> collect;
            if (StringUtils.isEmpty(pageReq.getChatbotName())) {
                collect = pictureResp.getAccounts().stream().filter(
                        account -> Objects.equals(pageReq.getStatus(), account.getStatus())
                ).collect(Collectors.toList());
            } else {
                collect = pictureResp.getAccounts().stream().filter(
                        account -> Objects.equals(account.getStatus(), pageReq.getStatus()) &&
                                StringUtils.equals(account.getOperator(), pageReq.getChatbotName())
                ).collect(Collectors.toList());
            }
            if (CollectionUtil.isNotEmpty(collect)) {
                result.add(pictureResp);
            }
        }
        return result;
    }

    private List<PictureResp> filterAccount(List<PictureResp> fileNameList, String chatbotName, List<String> chatbotAccounts) {
        List<PictureResp> result = new ArrayList<>();
        if (StringUtils.isEmpty(chatbotName) && ObjectUtil.isEmpty(chatbotAccounts))
            return fileNameList;
        if (StringUtils.isNotBlank(chatbotName)) {
            for (PictureResp pictureResp : fileNameList) {
                List<AccountResp> collect = new ArrayList<>();
                if (null != pictureResp.getAccounts()) {
                    collect = pictureResp.getAccounts().stream().filter(account -> StringUtils.equals(account.getOperator(), chatbotName)).collect(Collectors.toList());
                }
                if (CollectionUtil.isNotEmpty(collect)) {
                    result.add(pictureResp);
                }
            }
        }
        if (CollectionUtil.isNotEmpty(chatbotAccounts)) {
            String[] requireAccounts = chatbotAccounts.toArray(new String[0]);
            for (PictureResp pictureResp : fileNameList) {
                if (null != pictureResp.getAccounts()) {
                    String[] collect = pictureResp.getAccounts().stream().map(AccountResp::getChatbotAccount).toArray(String[]::new);
                    if (Arrays.equals(requireAccounts, collect)) {
                        result.add(pictureResp);
                    }
                }
            }
        }

        return result;
    }

    private List<PictureResp> filterFileName(List<PictureResp> list, String fileName) {
        if (!StringUtils.isNotBlank(fileName)) {
            return list;
        }
        return list.stream().filter(pictureResp -> pictureResp.getPictureName().contains(fileName)).collect(Collectors.toList());
    }

    private List<PictureResp> filterAllAccount(List<PictureResp> result, PageReq pageReq) {
        List<PictureResp> finalResult = new ArrayList<>();
        log.info("operators is {}", pageReq.getOperators());
        log.info("chatbotAccounts is {}", pageReq.getChatbotAccounts());
        if (null != pageReq.getOperators() && pageReq.getOperators().size() == 0) {
            return finalResult;
        }
        if (null == pageReq.getOperators()) {
            return result;
        }
        for (PictureResp pictureResp : result) {
            //List<String> collect = pictureResp.getAccounts().stream().map(AccountResp::getOperator).collect(Collectors.toList());
            //audioResp.getAccounts() 一定包含了4个通道的状态，前面步骤做了每个通道状态填充，filter后一定包含pageReq.getOperators()所有通道
            boolean statusResult = pictureResp.getAccounts().stream().filter(item -> pageReq.getOperators().contains(item.getOperator())).allMatch(e -> e.getStatus() == 2);
            if (statusResult) {
                finalResult.add(pictureResp);
            }
        }
        return finalResult;
    }

    @Override
    public List<GroupResp> manage(PageReq pageReq) {
        List<GroupResp> groupList = new ArrayList<>();
        String userId = SessionContextUtil.getUser().getUserId();
        LambdaQueryWrapper<PictureDo> wrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
            wrapper.eq(PictureDo::getCreator, userId);
        }
        List<PictureDo> pictureDos = pictureMapper.selectList(wrapper);
        List<PictureResp> vos = getPictureResps(pageReq, pictureDos, userId);
        List<PictureResp> result = filterResult(vos, pageReq);
        result = filterAllAccount(result, pageReq);

        if (CollUtil.isNotEmpty(result)) {
            Map<Long, List<PictureResp>> collectMap = result.stream().filter(p -> Objects.nonNull(p.getGroupId()) && 0 != p.getGroupId())
                    .collect(Collectors.groupingBy(PictureResp::getGroupId));
            for (Map.Entry<Long, List<PictureResp>> entry : collectMap.entrySet()) {
                GroupResp groupResp = new GroupResp();
                String groupName = groupMapper.selectById(entry.getKey()).getGroupName();
                groupResp.setId(entry.getKey());
                groupResp.setGroupName(groupName);
                groupResp.setNum(entry.getValue().size());
                groupList.add(groupResp);
            }
        }
        return groupList;
    }

    @NotNull
    private List<PictureResp> getPictureResps(PageReq pageReq, List<PictureDo> pictureDos, String userId) {
        List<PictureResp> vos = new ArrayList<>();
        List<AccountManagementResp> list = accountManagementApi.getAccountManagementlist(userId);
        pictureDos.forEach(picture -> {
            PictureResp vo = new PictureResp();
            BeanUtils.copyProperties(picture, vo);
            String pictureUrlId = picture.getPictureUrlId();
            List<AccountResp> accounts = examineResultService.getAccountList(pageReq, pictureUrlId, list);
            // 将accounts和预置的四个通道运营商的取交集，默认填充交集的数据，状态设置为未审核
            fillAccounts(accounts);
            vo.setAccounts(accounts);
            vos.add(vo);
        });
        return vos;
    }

    private void fillAccounts(List<AccountResp> accounts) {
        List<String> baseOperatorList = new ArrayList<>(Arrays.asList("硬核桃", "移动", "电信", "联通"));
        List<String> operatorList = accounts.stream().map(AccountResp::getOperator).collect(Collectors.toList());
        List<String> diff = baseOperatorList.stream().filter(item -> !operatorList.contains(item)).collect(Collectors.toList());
        diff.forEach(operator -> {
            AccountResp temp = new AccountResp();
            temp.setOperator(operator);
            temp.setStatus(1); // 待审核
            accounts.add(temp);
        });
    }
}
