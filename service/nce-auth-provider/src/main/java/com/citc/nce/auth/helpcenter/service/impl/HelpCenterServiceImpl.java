package com.citc.nce.auth.helpcenter.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.citc.nce.auth.helpcenter.Constant.HelpCenterConst;
import com.citc.nce.auth.helpcenter.dao.HelpArticleDao;
import com.citc.nce.auth.helpcenter.dao.HelpDirectoryDao;
import com.citc.nce.auth.helpcenter.entity.HelpArticleDo;
import com.citc.nce.auth.helpcenter.entity.HelpDirectoryDo;
import com.citc.nce.auth.helpcenter.service.HelpCenterService;
import com.citc.nce.auth.helpcenter.vo.ArticleVo;
import com.citc.nce.auth.helpcenter.vo.DirectoryVo;
import com.citc.nce.common.constants.Constants;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.util.JsonSameUtil;
import com.citc.nce.common.util.SessionContextUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author yy
 * @date 2024-05-06 15:29:42
 */
@Service
public class HelpCenterServiceImpl implements HelpCenterService {
    @Resource
    HelpDirectoryDao helpDirectoryDao;
    @Resource
    HelpArticleDao helpArticleDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishHelp(DirectoryVo directoryVo) {
        saveDirectory(directoryVo);
        HelpDirectoryDo helpDirectoryDo = getEditDirectory();
        if (StrUtil.isEmpty(helpDirectoryDo.getContent())) {
            throw new BizException("空目录不能发布");
        }
        Integer maxVersion = helpDirectoryDao.selectMaxVersion();
        int version = ObjectUtil.isEmpty(maxVersion) ? 0 : maxVersion + 1;
        JSONArray jsonArray = JSON.parseArray(helpDirectoryDo.getContent());
        if (jsonArray.isEmpty()) {
            throw new BizException("空目录不能发布");
        }
        //删选空目录
        filterEmptyDirectory(jsonArray, version);
        if (jsonArray.isEmpty()) {
            throw new BizException("没有有效文档不能发布");
        }
        String publisher = SessionContextUtil.getUser().getUserId();
        DateTime publishedTime = DateTime.now();
        helpDirectoryDo.setPublisher(publisher);
        helpDirectoryDo.setPublishTime(publishedTime);
        helpDirectoryDo.setStatus(HelpCenterConst.status_audited);
        helpDirectoryDao.updateById(helpDirectoryDo);
        HelpDirectoryDo publishHelpDirectoryDo = new HelpDirectoryDo();
        publishHelpDirectoryDo.setRecordType(HelpCenterConst.recordType_published);
        publishHelpDirectoryDo.setPublisher(publisher);
        publishHelpDirectoryDo.setPublishTime(publishedTime);
        publishHelpDirectoryDo.setVersion(version);
        publishHelpDirectoryDo.setContent(JSONObject.toJSONString(jsonArray));
        publishHelpDirectoryDo.setStatus(HelpCenterConst.status_audited);
        helpDirectoryDao.insert(publishHelpDirectoryDo);


    }

    void filterEmptyDirectory(JSONArray jsonArray, int version) {
        if (ObjectUtil.isEmpty(jsonArray) || jsonArray.isEmpty()) return;
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (HelpCenterConst.directory_type_article.equals(jsonObject.getInteger("type"))) {
                HelpArticleDo helpArticleDo = getArticleForComm(jsonObject.getString("id"));
                //检查文档是否为空
                boolean isEmpty = isEmptyArticle(helpArticleDo);
                if (isEmpty)
                    continue;
                publishArticle(version, helpArticleDo);
            } else {
                //检查是否空目录
                JSONArray subJsonArray = jsonObject.getJSONArray("children");
                if (ObjectUtil.isEmpty(subJsonArray) || subJsonArray.isEmpty()) {
                    continue;
                }
                filterEmptyDirectory(subJsonArray, version);
            }
        }


    }

    void publishArticle(int version, HelpArticleDo helpArticleDo) {
        helpArticleDo.setId(null);
        helpArticleDo.setVersion(version);
        helpArticleDo.setRecordType(HelpCenterConst.recordType_published);
        helpArticleDo.setCreateTime(null);
        helpArticleDo.setCreator(null);
        helpArticleDo.setUpdater(null);
        helpArticleDo.setUpdateTime(null);
        helpArticleDao.insert(helpArticleDo);
    }

    boolean isEmptyArticle(HelpArticleDo articleVo) {
        return ObjectUtil.isEmpty(articleVo) || StrUtil.isEmpty(articleVo.getContent());
    }

    HelpDirectoryDo getEditDirectory() {
        HelpDirectoryDo helpDirectoryDo = helpDirectoryDao.selectOne("record_type", HelpCenterConst.recordType_edit);
        if (ObjectUtil.isEmpty(helpDirectoryDo)) {
            helpDirectoryDo = new HelpDirectoryDo();
        }
        return helpDirectoryDo;
    }

    @Override
    public void saveDirectory(DirectoryVo directoryVo) {
        HelpDirectoryDo helpDirectoryDo = getEditDirectory();
        String oldContent = helpDirectoryDo.getContent();
        helpDirectoryDo.setContent(directoryVo.getContent());
        helpDirectoryDo.setRecordType(HelpCenterConst.recordType_edit);
        int result = 0;
        if (ObjectUtil.isEmpty(helpDirectoryDo.getId())) {
            helpDirectoryDo.setStatus(HelpCenterConst.status_draft);
            result = helpDirectoryDao.insert(helpDirectoryDo);
        } else {
            if (Objects.equals(helpDirectoryDo.getStatus(), HelpCenterConst.status_audited) && !JsonSameUtil.same(directoryVo.getContent(), oldContent, "")) {
                helpDirectoryDo.setStatus(HelpCenterConst.status_updated);
            }
            result = helpDirectoryDao.updateById(helpDirectoryDo);
        }
        if (result != 1) {
            throw new BizException("保存失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveArticle(ArticleVo articleVo) {
        HelpDirectoryDo helpDirectoryDo = getEditDirectory();
        if (StrUtil.isEmpty(helpDirectoryDo.getContent())) {
            throw new BizException("帮助中心目录为空");
        }
        if (!StrUtil.contains(helpDirectoryDo.getContent(), articleVo.getDocId())) {
            throw new BizException("帮助中心目录不包含当前文档id(docId)值");
        }
        HelpArticleDo helpArticleDo = getArticleForComm(articleVo.getDocId());
        if (ObjectUtil.isEmpty(helpArticleDo)) {
            helpArticleDo = new HelpArticleDo();
            helpArticleDo.setRecordType(HelpCenterConst.recordType_edit);
        }
        boolean isTitleSame = StrUtil.equals(articleVo.getTitle(), helpArticleDo.getTitle());
        boolean isContentSame = StrUtil.equals(helpArticleDo.getContent(), articleVo.getContent());
        helpArticleDo.setTitle(articleVo.getTitle());
        helpArticleDo.setDocId(articleVo.getDocId());
        helpArticleDo.setContent(articleVo.getContent());
        int result = 0;
        if (ObjectUtil.isEmpty(helpArticleDo.getId())) {
            result = helpArticleDao.insert(helpArticleDo);
        } else {
            result = helpArticleDao.updateById(helpArticleDo);
        }
        boolean directoryModified = false;
        if (!isTitleSame) {
            String directoryContent = updateDirectoryArticleTitle(articleVo.getTitle(), articleVo.getDocId(), helpDirectoryDo.getContent());
            helpDirectoryDo.setContent(directoryContent);
            directoryModified = true;
        }
        if (Objects.equals(helpDirectoryDo.getStatus(), HelpCenterConst.status_audited) && !(isContentSame && isTitleSame)) {
            helpDirectoryDo.setStatus(HelpCenterConst.status_updated);
            directoryModified = true;
        }
        if (directoryModified) {
            helpDirectoryDao.updateById(helpDirectoryDo);
        }
        if (result != 1) {
            throw new BizException("保存失败");
        }
    }

    String updateDirectoryArticleTitle(String newTitle, String docId, String directoryStr) {
        JSONArray jsonArray = JSON.parseArray(directoryStr);
        updateDirectoryArticleTitleRecursion(jsonArray, newTitle, docId);
        return jsonArray.toJSONString();

    }

    void updateDirectoryArticleTitleRecursion(JSONArray jsonArray, String newTitle, String docId) {
        if (ObjectUtil.isEmpty(jsonArray) || jsonArray.isEmpty()) return;
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (HelpCenterConst.directory_type_article.equals(jsonObject.getInteger("type"))) {
                if (StrUtil.equals(docId, jsonObject.getString("id"))) {
                    jsonObject.put("label", newTitle);
                    break;
                }
            } else {
                //检查是否空目录
                JSONArray subJsonArray = jsonObject.getJSONArray("children");
                if (ObjectUtil.isEmpty(subJsonArray) || subJsonArray.isEmpty()) {
                    continue;
                }
                updateDirectoryArticleTitleRecursion(subJsonArray, newTitle, docId);
            }
        }
    }

    @Override
    public DirectoryVo queryDirectory() {
        HelpDirectoryDo helpDirectoryDo = helpDirectoryDao.selectOne("record_type", HelpCenterConst.recordType_edit);
        if (ObjectUtil.isEmpty(helpDirectoryDo)) {
            return new DirectoryVo();
        }
        DirectoryVo directoryVo = BeanUtil.copyProperties(helpDirectoryDo, DirectoryVo.class);
        return directoryVo;
    }

    HelpArticleDo getArticleForComm(String docId) {
        LambdaQueryWrapper<HelpArticleDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HelpArticleDo::getRecordType, HelpCenterConst.recordType_edit);
        queryWrapper.eq(HelpArticleDo::getDocId, docId);
        queryWrapper.eq(HelpArticleDo::getDeleted, Constants.DELETE_STATUS_NOT);
        return helpArticleDao.selectOne(queryWrapper);
    }

    @Override
    public ArticleVo getArticle(String docId) {
        HelpArticleDo helpArticleDo = getArticleForComm(docId);
        if (ObjectUtil.isEmpty(helpArticleDo)) {
            return new ArticleVo();
        }
        return BeanUtil.copyProperties(helpArticleDo, ArticleVo.class);
    }

    @Override
    public DirectoryVo queryViewDirectory() {

        HelpDirectoryDo helpDirectoryDo = helpDirectoryDao.getLastPublished();
        if (ObjectUtil.isEmpty(helpDirectoryDo)) {
            return new DirectoryVo();
        }
        DirectoryVo directoryVo = BeanUtil.copyProperties(helpDirectoryDo, DirectoryVo.class);
        JSONArray jsonArray = JSON.parseArray(helpDirectoryDo.getContent());
        JSONArray articleJsonArray = new JSONArray();
        makeArticle(jsonArray, articleJsonArray, helpDirectoryDo.getVersion());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("directory", jsonArray);
        jsonObject.put("article", articleJsonArray);
        directoryVo.setContent(jsonObject.toJSONString());
        return directoryVo;
    }

    public HelpArticleDo getViewArticle(String docId, Integer version) {
        HelpArticleDo helpArticleDo = helpArticleDao.getLastPublishArticle(docId, version);
        return helpArticleDo;
    }

    void makeArticle(JSONArray jsonArray, JSONArray articleJsonArray, int version) {
        if (ObjectUtil.isEmpty(jsonArray) || jsonArray.isEmpty()) return;
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (HelpCenterConst.directory_type_article.equals(jsonObject.getInteger("type"))) {
                HelpArticleDo helpArticleDo = getViewArticle(jsonObject.getString("id"), version);
                //检查文档是否为空
                boolean isEmpty = isEmptyArticle(helpArticleDo);
                if (isEmpty) {
                    jsonArray.remove(i);
                    i--;
                    continue;
                }
                ArticleVo articleVo = BeanUtil.copyProperties(helpArticleDo, ArticleVo.class);
                articleJsonArray.add(JSONObject.toJSON(articleVo));
            } else {
                //检查是否空目录
                JSONArray subJsonArray = jsonObject.getJSONArray("children");
                if (ObjectUtil.isEmpty(subJsonArray) || subJsonArray.isEmpty()) {
                    jsonArray.remove(i);
                    i--;
                    continue;
                }
                makeArticle(subJsonArray, articleJsonArray, version);
                //检查删选后子目录是否为空，为空则删除当前目录
                if (ObjectUtil.isEmpty(subJsonArray) || subJsonArray.isEmpty()) {
                    jsonArray.remove(i);
                    i--;
                }
            }
        }
    }

    @Override
    public void deleteArticle(String docId) {
        HelpArticleDo helpArticleDo = getArticleForComm(docId);
        if (ObjectUtil.isEmpty(helpArticleDo)) {
            return;
        }
        helpArticleDao.logicDeleteByIds(Arrays.asList(helpArticleDo.getId()));
    }
}
