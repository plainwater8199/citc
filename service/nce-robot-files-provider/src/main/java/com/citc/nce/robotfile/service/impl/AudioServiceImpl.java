package com.citc.nce.robotfile.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.common.vo.tempStore.Csp4CustomerAudio;
import com.citc.nce.dto.AudioReq;
import com.citc.nce.dto.IdReq;
import com.citc.nce.dto.PageReq;
import com.citc.nce.filecenter.FileApi;
import com.citc.nce.filecenter.vo.DeleteReq;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import com.citc.nce.robotfile.configure.SuperAdministratorUserIdConfigure;
import com.citc.nce.robotfile.entity.AudioDo;
import com.citc.nce.robotfile.exp.RobotFileExp;
import com.citc.nce.robotfile.mapper.AudioMapper;
import com.citc.nce.robotfile.service.IAudioService;
import com.citc.nce.robotfile.service.IExamineResultService;
import com.citc.nce.vo.AccountResp;
import com.citc.nce.vo.AudioResp;
import com.citc.nce.vo.DeleteResp;
import com.citc.nce.vo.PageResultResp;
import com.citc.nce.vo.tempStore.UpdateTid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年6月29日16:29:48
 * @Version: 1.0
 * @Description: AudioServiceImpl
 */
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(SuperAdministratorUserIdConfigure.class)
@Slf4j
public class AudioServiceImpl extends ServiceImpl<AudioMapper, AudioDo> implements IAudioService {


    @Resource
    private AudioMapper audioMapper;

    @Resource
    private IExamineResultService examineResultService;

    @Resource
    private AccountManagementApi accountManagementApi;

    private final SuperAdministratorUserIdConfigure superAdministratorUserIdConfigure;

    @Resource
    private FileApi fileApi;


    @Override
    public PageResultResp<AudioResp> selectAll(PageReq pageReq) {
        PageResultResp<AudioResp> pageResult = new PageResultResp<>();
        if (pageReq.getPageSize() == 0 || pageReq.getPageNo() == 0) {
            pageReq.setPageNo(1);
            pageReq.setPageSize(5);
        }
        QueryWrapper<AudioDo> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("audio_upload_time");
        String userId = SessionContextUtil.getUser().getUserId();
        if (!StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
            wrapper.eq("creator", userId);
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
        List<AudioDo> records = audioMapper.selectList(wrapper);
        List<AudioResp> vos = new ArrayList<>();
        List<AccountManagementResp> list = accountManagementApi.getAccountManagementlist(userId);
        records.forEach(audio -> {
            AudioResp vo = new AudioResp();
            BeanUtils.copyProperties(audio, vo);
            String audioUrlId = audio.getAudioUrlId();
            List<AccountResp> accounts = examineResultService.getAccountList(pageReq, audioUrlId, list);
            // 将accounts和预置的四个通道运营商的取交集，默认填充交集的数据，状态设置为未审核
            fillAccounts(accounts);
            vo.setAccounts(accounts);
            vos.add(vo);
        });
        List<AudioResp> result = filterResult(vos, pageReq);
        result = filterAllAccount(result, pageReq);
        List<AudioResp> newResult = result.stream().skip((long) (pageReq.getPageNo() - 1) * pageReq.getPageSize())
                .limit(pageReq.getPageSize()).collect(Collectors.toList());
        pageResult.setTotal((long) result.size());
        if (result.size() == 0) {
            pageResult.setTotal(0L);
        }
        pageResult.setList(newResult);
        return pageResult;
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

    private List<AudioResp> filterResult(List<AudioResp> list, PageReq pageReq) {
        //筛选文件名
        List<AudioResp> fileNameList = filterFileName(list, pageReq.getFileName());
        //筛选账号
        List<AudioResp> accountList = filterAccount(fileNameList, pageReq.getChatbotName());
        //筛选状态
        return filterStatus(accountList, pageReq);
    }

    private List<AudioResp> filterStatus(List<AudioResp> accountList, PageReq pageReq) {
        List<AudioResp> result = new ArrayList<>();
        if (null == pageReq.getStatus() || pageReq.getStatus() == 0) {
            return accountList;
        }
        for (AudioResp audioResp : accountList) {
            // 查询状态的同时，如果chatbotName不为空，需要过滤
            List<AccountResp> collect;
            if (StringUtils.isEmpty(pageReq.getChatbotName())) {
                collect = audioResp.getAccounts().stream().filter(
                        account -> Objects.equals(pageReq.getStatus(), account.getStatus())
                ).collect(Collectors.toList());
            } else {
                collect = audioResp.getAccounts().stream().filter(
                        account -> Objects.equals(account.getStatus(), pageReq.getStatus()) &&
                                StringUtils.equals(account.getOperator(), pageReq.getChatbotName())
                ).collect(Collectors.toList());
            }
            if (CollectionUtil.isNotEmpty(collect)) {
                result.add(audioResp);
            }
        }
        return result;
    }

    private List<AudioResp> filterAccount(List<AudioResp> fileNameList, String chatbotName) {
        List<AudioResp> result = new ArrayList<>();
        if (!StringUtils.isNotBlank(chatbotName)) {
            return fileNameList;
        }
        for (AudioResp audioResp : fileNameList) {
            List<AccountResp> collect = new ArrayList<>();
            if (null != audioResp.getAccounts()) {
                collect = audioResp.getAccounts().stream().filter(account -> StringUtils.equals(account.getOperator(), chatbotName)).collect(Collectors.toList());
            }
            if (CollectionUtil.isNotEmpty(collect)) {
                result.add(audioResp);
            }
        }
        return result;
    }

    private List<AudioResp> filterFileName(List<AudioResp> list, String fileName) {
        if (!StringUtils.isNotBlank(fileName)) {
            return list;
        }
        return list.stream().filter(audioResp -> audioResp.getAudioName().contains(fileName)).collect(Collectors.toList());
    }


    private List<AudioResp> filterAllAccount(List<AudioResp> result, PageReq pageReq) {
        List<AudioResp> finalResult = new ArrayList<>();
        if (null != pageReq.getOperators() && pageReq.getOperators().size() == 0) {
            return finalResult;
        }
        if (null == pageReq.getOperators()) {
            return result;
        }
        for (AudioResp audioResp : result) {
            //List<String> collect = audioResp.getAccounts().stream().map(AccountResp::getOperator).collect(Collectors.toList());
            //audioResp.getAccounts() 一定包含了4个通道的状态，前面步骤做了每个通道状态填充，filter后一定包含pageReq.getOperators()所有通道
            boolean statusResult = audioResp.getAccounts().stream().filter(item->pageReq.getOperators().contains(item.getOperator())).allMatch(e -> e.getStatus() == 2);
            if (statusResult) {
                finalResult.add(audioResp);
            }
        }
        return finalResult;
    }

    private AudioResp change(AudioDo audio) {
        AudioResp vo = new AudioResp();
        BeanUtils.copyProperties(audio, vo);
        return vo;
    }

    @Override
    @Transactional
    public void saveAudio(AudioReq audioDto) {
        String audioName = audioDto.getAudioName();
        String fileFormat = audioName.substring(audioName.lastIndexOf(".") + 1);
        AudioDo audio = new AudioDo();
        //保存时判断是否已经保存了
        QueryWrapper<AudioDo> wrapper = new QueryWrapper<>();
        wrapper.eq("audio_url_id", audioDto.getAudioUrlId());
        AudioDo exit = audioMapper.selectOne(wrapper);
        //如果存在就直接结束
        if (exit == null) {
            audio.setAudioUrlId(audioDto.getAudioUrlId());
            audio.setAudioName(audioName);
            audio.setAudioSize(audioDto.getAudioSize());
            audio.setAudioUploadTime(new Date());
            audio.setAudioDuration(audioDto.getAudioDuration());
            audio.setAudioFormat(fileFormat);
            if (!save(audio)) throw new BizException(RobotFileExp.SQL_ERROR);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAudioList(List<AudioReq> audioReq) {
        if (CollectionUtils.isEmpty(audioReq)) {
            return;
        }
        List<String> audioUrlIdList = new ArrayList<>();
        HashMap<String, AudioDo> insertMap = new HashMap<>();
        for (AudioReq audioDto : audioReq) {
            audioUrlIdList.add(audioDto.getAudioUrlId());
            String audioName = audioDto.getAudioName();
            String fileFormat = audioName.substring(audioName.lastIndexOf(".") + 1);
            AudioDo audio = new AudioDo();
            audio.setAudioUrlId(audioDto.getAudioUrlId());
            audio.setAudioName(audioName);
            audio.setAudioSize(audioDto.getAudioSize());
            audio.setAudioUploadTime(new Date());
            audio.setAudioDuration(audioDto.getAudioDuration());
            audio.setAudioFormat(fileFormat);
            audio.setDeleted(0);
            audio.setDeleteTime(new Date());
            audio.setUsed(0);
            insertMap.put(audioDto.getAudioUrlId(), audio);
        }
        try {
            //保存时判断是否已经保存了
            QueryWrapper<AudioDo> wrapper = new QueryWrapper<>();
            wrapper.in("audio_url_id", audioUrlIdList);
            List<AudioDo> audioDoList = audioMapper.selectList(wrapper);
            // 去重
            if (CollectionUtils.isNotEmpty(audioDoList)) {
                audioDoList.forEach(audio -> insertMap.remove(audio.getAudioUrlId()));
            }
            Iterator<Map.Entry<String, AudioDo>> iterator = insertMap.entrySet().iterator();
            List<AudioDo> insertList = new ArrayList<>();
            while (iterator.hasNext()) {
                insertList.add(iterator.next().getValue());
            }
            if (CollectionUtils.isNotEmpty(insertList)) {
                audioMapper.insertBatch(insertList);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<Long, Csp4CustomerAudio> saveAudioListCsp4customer(List<Csp4CustomerAudio> list) {
        if (CollectionUtils.isEmpty(list)) {
            return new HashMap<>();
        }
        //用户收集新id
        Map<Long, Csp4CustomerAudio> returnMap = list.stream()
                .collect(Collectors.toMap(Csp4CustomerAudio::getAudioId, Function.identity()));
        //转换为保存数据库对象用于快速过滤已存在数据
        Map<String, AudioDo> insertMap = list.stream().map(s -> {
            AudioDo audio = new AudioDo();
            audio.setAudioUrlId(s.getFileId());
            audio.setAudioName(s.getName());
            audio.setAudioSize(s.getSize());
            audio.setAudioUploadTime(new Date());
            audio.setAudioDuration(s.getDuration());
            audio.setAudioFormat(s.getFormat());
            audio.setDeleted(0);
            audio.setDeleteTime(new Date());
            audio.setUsed(0);
            //备份原始id
            audio.setOldId(s.getAudioId());
            return audio;
        }).collect(Collectors.toMap(AudioDo::getAudioUrlId, Function.identity()));

     /*   //保存时判断是否已经保存了
        QueryWrapper<AudioDo> wrapper = new QueryWrapper<>();
        wrapper.in("audio_url_id", insertMap.keySet());
        List<AudioDo> dbList = audioMapper.selectList(wrapper);
        // 过滤已经保存过的数据
        if (CollectionUtils.isNotEmpty(dbList)) {
            for (AudioDo audio : dbList) {
                AudioDo audioDo = insertMap.get(audio.getAudioUrlId());
                if (Objects.nonNull(audioDo)) {
                    insertMap.remove(audio.getAudioUrlId());
                    //回写新id;
                    Csp4CustomerAudio csp4CustomerAudio = returnMap.get(audioDo.getOldId());
                    if (Objects.isNull(csp4CustomerAudio)) {
                        log.warn("Csp4CustomerAudio lost file , oldId: {}", audioDo.getOldId());
                        continue;
                    }
                    csp4CustomerAudio.setNewId(audio.getId());
                }
            }
        }*/
        //保存
        List<AudioDo> insertList = new ArrayList<>(insertMap.values());
        if (CollectionUtils.isNotEmpty(insertList)) {
            insertList.forEach(s->audioMapper.insert(s));
        }
        //回写新id;
        for (AudioDo audio : insertList) {
            Csp4CustomerAudio csp4CustomerAudio = returnMap.get(audio.getOldId());
            if (Objects.isNull(csp4CustomerAudio)) {
                log.warn("Csp4CustomerAudio lost file , oldId: {}", audio.getOldId());
                continue;
            }
            csp4CustomerAudio.setNewId(audio.getId());
        }
        return returnMap;

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeleteResp deleteAudio(IdReq req) {
        if (req.getId() == 0) {
            throw new BizException(GlobalErrorCode.BAD_REQUEST);
        }
        AudioDo audioDo = audioMapper.selectById(req.getId());
        if (Objects.isNull(audioDo)) return null;
        if (!SessionContextUtil.getUser().getUserId().equals(audioDo.getCreator())) {
            throw new BizException("不能删除别人的资源");
        }
        List<Long> ids = new ArrayList<>();
        ids.add(req.getId());
        int count = audioMapper.logicDeleteByIds(ids);
        if (count <= 0) throw new BizException(RobotFileExp.SQL_ERROR);
        DeleteResp resp = new DeleteResp();
        List<String> list = new ArrayList<>();
        list.add(audioDo.getAudioUrlId());
        resp.setFileUrlIds(list);
        DeleteReq deleteReq = new DeleteReq();
        deleteReq.setFileUrlIds(resp.getFileUrlIds());
        fileApi.deleteFile(deleteReq);
        return resp;
    }

    @Override
    public AudioResp getFileInfoByAudioUrlId(AudioReq audioReq) {
        LambdaQueryWrapperX<AudioDo> queryWrapperX = new LambdaQueryWrapperX<>();
        queryWrapperX.eq(AudioDo::getAudioUrlId, audioReq.getAudioUrlId())
                .eq(AudioDo::getDeleted, 0);
        List<AudioDo> audioDos = audioMapper.selectList(queryWrapperX);
        AudioResp vo = new AudioResp();
        if (CollectionUtil.isNotEmpty(audioDos)) {
            BeanUtils.copyProperties(audioDos.get(0), vo);
        }
        return vo;
    }

}
