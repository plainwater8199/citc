package com.citc.nce.auth.cardstyle.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.citc.nce.auth.cardstyle.dao.CardStyleDao;
import com.citc.nce.auth.cardstyle.entity.CardStyleDo;
import com.citc.nce.auth.cardstyle.exception.CardStyleCode;
import com.citc.nce.auth.cardstyle.service.CardStyleService;
import com.citc.nce.auth.cardstyle.vo.*;
import com.citc.nce.auth.configure.SuperAdministratorUserIdConfigure;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.filecenter.FileApi;
import com.citc.nce.filecenter.vo.UploadReq;
import com.citc.nce.filecenter.vo.UploadResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * @Author: yangchuang
 * @Date: 2022/8/15 16:32
 * @Version: 1.0
 * @Description:
 */
@Service
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(SuperAdministratorUserIdConfigure.class)
public class CardStyleServiceImpl implements CardStyleService {

    private final SuperAdministratorUserIdConfigure superAdministratorUserIdConfigure;
    @Resource
    private CardStyleDao cardStyleDao;

    @Resource
    private FileApi fileApi;


    @Override
    public PageResultResp getCardStyles(PageParam pageParam) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        wrapper.eq("is_show", 0);
        String userId = SessionContextUtil.getUser().getUserId();
        if (!StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
            wrapper.eq("creator", userId);
        }
        wrapper.orderByDesc("create_time");
        PageResult<CardStyleDo> cardStyleDoPageResult = cardStyleDao.selectPage(pageParam, wrapper);
        List<CardStyleResp> cardStyleResps = BeanUtil.copyToList(cardStyleDoPageResult.getList(), CardStyleResp.class);
        return new PageResultResp(cardStyleResps, cardStyleDoPageResult.getTotal(), pageParam.getPageNo());
    }

    @Override
    public Long saveCardStyle(CardStyleReq cardStyleReq) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        wrapper.eq("style_name", cardStyleReq.getStyleName());
        String userId = SessionContextUtil.getUser().getUserId();
        if (!StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
            wrapper.eq("creator", userId);
        }
        List<CardStyleDo> cardStyleDos = cardStyleDao.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(cardStyleDos)) {
            throw new BizException(CardStyleCode.VARIABLE_BAD_CARDSTYLE_NAME);
        }
        CardStyleDo cardStyleDo = new CardStyleDo();
        BeanUtil.copyProperties(cardStyleReq, cardStyleDo);
        log.info("开始生成.css文件", cardStyleReq.getStyleCss());
        String urlId = stringToCssFile(cardStyleReq.getStyleCss());
        cardStyleDo.setFileId(urlId);
        log.info("css文件上传生成fileId{}", urlId);
        cardStyleDao.insert(cardStyleDo);
        return cardStyleDo.getId();
    }

    @Override
    public int updateCardStyle(CardStyleEditReq cardStyleEditReq) {
        CardStyleDo select = cardStyleDao.selectById(cardStyleEditReq.getId());
        if (Objects.isNull(select)){
            throw new BizException("卡片不存在");
        }
        if (!SessionContextUtil.getUser().getUserId().equals(select.getCreator())){
            throw new BizException("不能修改别人的卡片");
        }
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        wrapper.eq("style_name", cardStyleEditReq.getStyleName());
        wrapper.ne("id", cardStyleEditReq.getId());
        String userId = SessionContextUtil.getUser().getUserId();
        if (!StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
            wrapper.eq("creator", userId);
        }
        List<CardStyleDo> cardStyleDos = cardStyleDao.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(cardStyleDos)) {
            for (CardStyleDo cardStyleDo : cardStyleDos) {
                if (!cardStyleEditReq.getId().equals(cardStyleDo.getId())) {
                    throw new BizException(CardStyleCode.VARIABLE_BAD_CARDSTYLE_NAME);
                }
            }
        }
        CardStyleDo cardStyleDo = new CardStyleDo();
        BeanUtil.copyProperties(cardStyleEditReq, cardStyleDo);
        log.info("开始生成.css文件", cardStyleEditReq.getStyleCss());
        String urlId = stringToCssFile(cardStyleEditReq.getStyleCss());
        cardStyleDo.setFileId(urlId);
        log.info("css文件上传生成fileId{}", urlId);
        return cardStyleDao.updateById(cardStyleDo);
    }

    @Override
    public int delCardStyleById(CardStyleOneReq cardStyleOneReq) {
        CardStyleDo select = cardStyleDao.selectById(cardStyleOneReq.getId());
        if (Objects.isNull(select)){
            throw new BizException("卡片不存在");
        }
        if (!SessionContextUtil.getUser().getUserId().equals(select.getCreator())){
            throw new BizException("不能删除别人的卡片");
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("id", cardStyleOneReq.getId());
        map.put("deleted", 1);
        map.put("deleteTime", DateUtil.date());
        return cardStyleDao.delCardStyleById(map);
    }

    @Override
    public CardStyleResp getCardStyleById(CardStyleOneReq cardStyleOneReq) {
        CardStyleDo cardStyleDo = cardStyleDao.selectOne(Wrappers.<CardStyleDo>lambdaQuery()
                .eq(CardStyleDo::getId, cardStyleOneReq.getId())
                .eq(CardStyleDo::getDeleted, 0));
        if (cardStyleDo == null) {
            return null;
        }
        if (!SessionContextUtil.getUser().getUserId().equals(cardStyleDo.getCreator())) {
            throw new BizException("卡片样式不是自己的");
        }
        CardStyleResp cardStyleResp = BeanUtil.copyProperties(cardStyleDo, CardStyleResp.class);
        return cardStyleResp;
    }

    @Override
    public List<CardStyleTreeResp> getTreeList() {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        wrapper.isNotNull("is_show");
        String userId = SessionContextUtil.getUser().getUserId();
        if (!StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
            wrapper.eq("creator", userId);
        }
        wrapper.orderByDesc("create_time");
        List<CardStyleDo> cardStyleDos = cardStyleDao.selectList(wrapper);
        List<CardStyleTreeResp> cardStyleTreeResps = BeanUtil.copyToList(cardStyleDos, CardStyleTreeResp.class);
        return cardStyleTreeResps;
    }

    @Override
    public CardStyleResp getCardStyleByIdInner(CardStyleOneReq cardStyleOneReq) {
        CardStyleDo cardStyleDo = cardStyleDao.selectOne(Wrappers.<CardStyleDo>lambdaQuery()
                .eq(CardStyleDo::getId, cardStyleOneReq.getId())
                .eq(CardStyleDo::getDeleted, 0));
        if (cardStyleDo == null) {
            return null;
        }
        CardStyleResp cardStyleResp = BeanUtil.copyProperties(cardStyleDo, CardStyleResp.class);
        return cardStyleResp;
    }

    public String stringToCssFile(String styleCss) {
        File tempFile = null;
        try {
            tempFile = File.createTempFile(UUID.fastUUID().toString(), ".css");
            FileUtils.writeStringToFile(tempFile, styleCss, "utf-8");
            CommonsMultipartFile multipartFile = getMultipartFile(tempFile);
            UploadReq uploadReq = new UploadReq();
            uploadReq.setFile(multipartFile);
            uploadReq.setSceneId("codeincodeservice");
            List<UploadResp> uploadResps = fileApi.uploadFile(uploadReq);
            boolean delete = tempFile.delete();
            if (!delete) {
                log.info("删除失败!");
            }

            if (CollectionUtils.isNotEmpty(uploadResps)) {
                UploadResp uploadResp = uploadResps.get(0);
                if (uploadResp != null) {
                    return uploadResp.getUrlId();
                }
            }
        } catch (IOException e) {
            log.error("css文件上传错误{}", e);
        }

        return "";
    }

    public CommonsMultipartFile getMultipartFile(File file) {
        FileItem item = new DiskFileItemFactory().createItem("file"
                , MediaType.MULTIPART_FORM_DATA_VALUE
                , true
                , file.getName());
        try (InputStream input = new FileInputStream(file);
             OutputStream os = item.getOutputStream()) {
            // 流转移
            IOUtils.copy(input, os);
        } catch (Exception e) {
            log.error("文件转换错误{}", e);
        }

        return new CommonsMultipartFile(item);
    }
}
