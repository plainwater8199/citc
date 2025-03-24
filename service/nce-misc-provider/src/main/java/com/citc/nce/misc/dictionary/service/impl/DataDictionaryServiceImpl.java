package com.citc.nce.misc.dictionary.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.misc.constant.MiscErrorCode;
import com.citc.nce.misc.dictionary.entity.DataDictionaryDo;
import com.citc.nce.misc.dictionary.entity.DataDictionaryTypeDo;
import com.citc.nce.misc.dictionary.mapper.DataDictionaryMapper;
import com.citc.nce.misc.dictionary.mapper.DataDictionaryTypeMapper;
import com.citc.nce.misc.dictionary.service.DataDictionaryService;
import com.citc.nce.misc.dictionary.vo.req.*;
import com.citc.nce.misc.dictionary.vo.resp.DictionaryResp;
import com.citc.nce.misc.vo.DataDictionaryResp;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/7/29 17:24
 * @Version 1.0
 * @Description:
 */
@Service
@Slf4j
public class DataDictionaryServiceImpl implements DataDictionaryService {

    @Resource
    private DataDictionaryMapper dataDictionaryMapper;

    @Resource
    private DataDictionaryTypeMapper dataDictionaryTypeMapper;

    @Override
    public List<DictionaryResp> getDictionaryByType(DictionaryReq req) {
        LambdaQueryWrapperX<DataDictionaryDo> lambdaQueryWrapperX = new LambdaQueryWrapperX<>();
        lambdaQueryWrapperX.eq(DataDictionaryDo::getTypeCode, req.getCode())
                .orderByAsc(DataDictionaryDo::getSort);
        List<DataDictionaryDo> dataDictionaryDos = dataDictionaryMapper.selectList(lambdaQueryWrapperX);
        /**
         * 2023-4-24
         * 过滤字典重复字段：code
         */
        List<DictionaryResp> dictionaryResps = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(dataDictionaryDos)) {
            ArrayList<DataDictionaryDo> collect = dataDictionaryDos.stream().collect(
                    Collectors.collectingAndThen(
                            Collectors.toCollection(
                                    () -> new TreeSet<>(Comparator.comparing(DataDictionaryDo::getCode))), ArrayList::new));
            dictionaryResps = BeanUtil.copyToList(collect, DictionaryResp.class);
        } else {
            dictionaryResps = BeanUtil.copyToList(dataDictionaryDos, DictionaryResp.class);
        }
        return dictionaryResps;
    }

    @Override
    @Transactional()
    public boolean importDataDictionary(DataDictionaryListReq req) {
        List<DataDictionaryTypeReq> list = req.getList();
        if (CollectionUtils.isEmpty(list)) {
            log.error("error message is parameter list is empty" + list);
            throw new BizException(MiscErrorCode.PARAMETER_MISS);
        }
        list.forEach(i -> {
            DataDictionaryTypeDo typeDo = new DataDictionaryTypeDo().setTypeName(i.getTypeName());
            dataDictionaryTypeMapper.insert(typeDo);
            List<DataDictionaryReq> dataList = i.getDataDictionaryReqList();
            for (int j = 0; j < dataList.size(); j++) {
                DataDictionaryDo data = new DataDictionaryDo();
                BeanUtils.copyProperties(dataList.get(j), data);
                data.setDicTypeId(typeDo.getId());
                data.setSort(j + 1);
                dataDictionaryMapper.insert(data);
            }
        });
        return true;
    }

    @Override
    public Map<String, DictionaryResp> getDictionaryInfoByType(DictionaryReq req) {
        LambdaQueryWrapperX<DataDictionaryDo> lambdaQueryWrapperX = new LambdaQueryWrapperX<>();
        lambdaQueryWrapperX.eq(DataDictionaryDo::getTypeCode, req.getCode())
                .orderByAsc(DataDictionaryDo::getSort);
        List<DataDictionaryDo> list = dataDictionaryMapper.selectList(lambdaQueryWrapperX);
        Map<String, DictionaryResp> data = new HashMap<>();
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(e -> data.put(e.getCode(), JSONObject.parseObject(JSONObject.toJSONString(e)).toJavaObject(DictionaryResp.class)));
        }
        return data;
    }

    @Override
    public void updateApiTypeDictionary(DeleteReq req) {
        dataDictionaryMapper.updateDeletedByDicTypeId(req.getDeleted());
    }


    private List<DataDictionaryResp> createTree(List<DataDictionaryResp> lists, Long pid) {
        List<DataDictionaryResp> tree = new ArrayList<>();
        for (DataDictionaryResp resp : lists) {
            if (resp.getParentId().equals(pid)) {
                resp.setChildren(createTree(lists, resp.getId()));
                tree.add(resp);
            }
        }
        return tree;
    }
}
