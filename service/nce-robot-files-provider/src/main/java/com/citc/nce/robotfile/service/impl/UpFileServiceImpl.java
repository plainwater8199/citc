package com.citc.nce.robotfile.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.dto.IdReq;
import com.citc.nce.dto.PageReq;
import com.citc.nce.dto.UpFileReq;
import com.citc.nce.robotfile.configure.SuperAdministratorUserIdConfigure;
import com.citc.nce.robotfile.entity.UpFileDo;
import com.citc.nce.robotfile.exp.RobotFileExp;
import com.citc.nce.robotfile.mapper.UpFileMapper;
import com.citc.nce.robotfile.service.IExamineResultService;
import com.citc.nce.robotfile.service.IUpFileService;
import com.citc.nce.vo.AccountResp;
import com.citc.nce.vo.DeleteResp;
import com.citc.nce.vo.PageResultResp;
import com.citc.nce.vo.UpFileResp;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年6月29日16:29:48
 * @Version: 1.0
 * @Description: UpFileServiceImpl
 */
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(SuperAdministratorUserIdConfigure.class)
public class UpFileServiceImpl extends ServiceImpl<UpFileMapper, UpFileDo> implements IUpFileService {

    @Resource
    private UpFileMapper upFileMapper;

    @Resource
    private IExamineResultService examineResultService;


    @Resource
    private AccountManagementApi accountManagementApi;

    private final SuperAdministratorUserIdConfigure superAdministratorUserIdConfigure;

    @Override
    @Transactional
    public void saveUpFile(UpFileReq upFileDto) {
        //检测是否已存在
        QueryWrapper<UpFileDo> wrapper = new QueryWrapper<>();
        wrapper.eq("file_url_id",upFileDto.getFileUrlId());
        UpFileDo exit = upFileMapper.selectOne(wrapper);
        if (exit == null) {
            String fileName = upFileDto.getFileName();
            UpFileDo upFile = new UpFileDo();
            upFile.setFileUrlId(upFileDto.getFileUrlId());
            upFile.setFileName(upFileDto.getFileName());
            upFile.setFileSize(upFileDto.getFileSize());
            String fileFormat = fileName.substring(fileName.lastIndexOf(".") + 1);
            upFile.setFileFormat(fileFormat);
            upFile.setFileUploadTime(new Date());
            if (!save(upFile)) throw new BizException(RobotFileExp.SQL_ERROR);
        }
    }

    @Override
    public PageResultResp<UpFileResp> selectAll(PageReq req) {
        PageResultResp<UpFileResp> pageResult = new PageResultResp<>();
        if (req.getPageNo() == 0 || req.getPageSize() == 0) {
            req.setPageNo(1);
            req.setPageSize(5);
        }
        QueryWrapper<UpFileDo> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", 0);
        wrapper.orderByDesc("file_upload_time");
        String userId = SessionContextUtil.getUser().getUserId();
        if (!StringUtils.equals(userId,superAdministratorUserIdConfigure.getSuperAdministrator())){
            wrapper.eq("creator",userId);
        }
        if (!StringUtils.isEmpty(req.getTabs())){
            if (!CollectionUtil.isEmpty(req.getMaterialIds())){
                wrapper.in("id",req.getMaterialIds());
            }else {
                return pageResult;
            }
        }
        List<UpFileDo> records = upFileMapper.selectList(wrapper);
        List<UpFileResp> vos = new ArrayList<>();
        List<AccountManagementResp> list = accountManagementApi.getAccountManagementlist(userId);
        records.forEach(upFile -> {
            UpFileResp vo = new UpFileResp();
            BeanUtils.copyProperties(upFile, vo);
            String fileUrlId = upFile.getFileUrlId();
            List<AccountResp> accountList = examineResultService.getAccountList(req, fileUrlId,list);
            vo.setAccounts(accountList);
            vos.add(vo);
        });
        List<UpFileResp> result = filterResult(vos, req);
        result = filterAllAccount(result, req);
        List<UpFileResp> newResult = result.stream().skip((long) (req.getPageNo() - 1) * req.getPageSize() )
                .limit(req.getPageSize()).collect(Collectors.toList());
        pageResult.setTotal((long)result.size());
        if (result.size() == 0){
            pageResult.setTotal(0L);
        }
        pageResult.setList(newResult);
        return pageResult;
    }


    private List<UpFileResp> filterResult(List<UpFileResp> list, PageReq pageReq) {
        //筛选文件名
        List<UpFileResp> fileNameList= filterFileName(list,pageReq.getFileName());
        //筛选账号
        List<UpFileResp> accountList= filterAccount(fileNameList,pageReq.getChatbotName());
        //筛选状态
        return filterStatus(accountList,pageReq.getStatus());
    }

    private List<UpFileResp> filterStatus(List<UpFileResp> accountList, Integer status) {
        List<UpFileResp> result = new ArrayList<>();
        if (null == status || status == 0){
            return accountList;
        }
        for (UpFileResp upFileResp : accountList) {
            List<AccountResp> collect = upFileResp.getAccounts().stream().filter(account -> account.getStatus() == (long)status).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(collect)){
                result.add(upFileResp);
            }
        }
        return result;
    }

    private List<UpFileResp> filterAccount(List<UpFileResp> fileNameList, String chatbotName) {
        List<UpFileResp> result = new ArrayList<>();
        if (!StringUtils.isNotBlank(chatbotName)){
            return fileNameList;
        }
        for (UpFileResp upFileResp : fileNameList) {
            List<AccountResp> collect = new ArrayList<>();
            if(null != upFileResp.getAccounts()){
                collect = upFileResp.getAccounts().stream().filter(account -> StringUtils.equals(account.getOperator(), chatbotName)).collect(Collectors.toList());
            }
            if (CollectionUtil.isNotEmpty(collect)){
                result.add(upFileResp);
            }
        }
        return result;
    }

    private List<UpFileResp> filterFileName(List<UpFileResp> list, String fileName) {
        if (!StringUtils.isNotBlank(fileName)){
            return list;
        }
        return list.stream().filter(upFileResp -> upFileResp.getFileName().contains(fileName)).collect(Collectors.toList());
    }


    private List<UpFileResp> filterAllAccount(List<UpFileResp> result,PageReq pageReq){
        List<UpFileResp> finalResult = new ArrayList<>();
        if (null != pageReq.getOperators() && pageReq.getOperators().size() == 0){
            return finalResult;
        }
        if (null == pageReq.getOperators()){
            return result;
        }
        for (UpFileResp upFileResp : result) {
            List<String> collect = upFileResp.getAccounts().stream().map(AccountResp::getOperator).collect(Collectors.toList());
            boolean statusResult = upFileResp.getAccounts().stream().allMatch(e -> e.getStatus() == 2);
            if (collect.containsAll(pageReq.getOperators()) && statusResult){
                finalResult.add(upFileResp);
            }
        }
        return finalResult;
    }

    @Override
    public DeleteResp deleteUpFile(IdReq req) {
        if (req == null) {
            throw new BizException(RobotFileExp.BAD_REQUEST);
        }
        UpFileDo fileDo = upFileMapper.selectById(req.getId());
        List<Long> ids = new ArrayList<>();
        ids.add(req.getId());
        int count = upFileMapper.logicDeleteByIds(ids);
        if (count <= 0 ) {
            throw new BizException(RobotFileExp.SQL_ERROR);
        }
        if (fileDo!=null){
            List<String> list = new ArrayList<>();
            DeleteResp resp = new DeleteResp();
            list.add(fileDo.getFileUrlId());
            resp.setFileUrlIds(list);
            return resp;
        }
        throw new BizException(RobotFileExp.SQL_ERROR);
    }
}
