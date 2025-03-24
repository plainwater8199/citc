package com.citc.nce.filecenter;

import com.citc.nce.filecenter.tempStore.SaveMaterialReq;
import com.citc.nce.filecenter.tempStore.SaveMaterialResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/6/24 11:26
 * @Version: 1.0
 * @Description:
 */
@FeignClient(value = "filecenter-service", contextId = "TempStoreMaterialApi", url = "${filecenter:}")
public interface TempStoreMaterialApi {

    /**
     * 模板商城保存素材（不送审）
     */
    @PostMapping(value = "/file/tempStoreMaterial/save")
    SaveMaterialResult saveTempStoreMaterial(@RequestBody @Valid SaveMaterialReq saveMaterialReq);

    /**
     * 模板商城保存素材（不送审）
     */
    @GetMapping(value = "/file/tempStoreMaterial/commit/{mapKey}")
    void commit(@PathVariable("mapKey") Long mapKey);

    /**
     * 模板商城保存素材（不送审）
     */
    @GetMapping(value = "/file/tempStoreMaterial/cancel/{mapKey}")
    void cancel(@PathVariable("mapKey") Long mapKey);

    /**
     * 模板商城保存卡片样式中的图片
     * @param oldFileUUid
     * @return
     */
    @PostMapping(value = "/file/tempStoreMaterial/saveTempStoreCardStyleImg/{oldFileUUid}")
    String saveTempStoreCardStyleImg(@PathVariable("oldFileUUid") String oldFileUUid);
}
