package com.citc.nce.filecenter.service;

import com.citc.nce.filecenter.tempStore.SaveMaterialReq;
import com.citc.nce.filecenter.tempStore.SaveMaterialResult;

public interface TempStoreFileService {
    SaveMaterialResult saveTempStoreMaterial(SaveMaterialReq saveMaterialReq);

    void commit(Long mapKey);

    void cancel(Long mapKey);

    String saveTempStoreCardStyleImg(String oldFileUUid);
}
