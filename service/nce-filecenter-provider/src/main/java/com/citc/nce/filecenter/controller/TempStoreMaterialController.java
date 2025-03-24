package com.citc.nce.filecenter.controller;

import com.citc.nce.filecenter.TempStoreMaterialApi;
import com.citc.nce.filecenter.service.TempStoreFileService;
import com.citc.nce.filecenter.tempStore.SaveMaterialReq;
import com.citc.nce.filecenter.tempStore.SaveMaterialResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @Author: bydud$
 * @Date: 2024/5/11$
 * @Description: This is a sample file header.
 */
@RestController
@Slf4j
@AllArgsConstructor
public class TempStoreMaterialController implements TempStoreMaterialApi {

    private final TempStoreFileService tempStoreFileService;

    @Override
    public SaveMaterialResult saveTempStoreMaterial(@RequestBody @Valid SaveMaterialReq saveMaterialReq) {
        return tempStoreFileService.saveTempStoreMaterial(saveMaterialReq);
    }

    @Override
    public void commit(Long mapKey) {
        tempStoreFileService.commit(mapKey);
    }

    @Override
    public void cancel(Long mapKey) {
        tempStoreFileService.cancel(mapKey);
    }

    @Override
    public String saveTempStoreCardStyleImg(String oldFileUUid) {
        return tempStoreFileService.saveTempStoreCardStyleImg(oldFileUUid);
    }
}
