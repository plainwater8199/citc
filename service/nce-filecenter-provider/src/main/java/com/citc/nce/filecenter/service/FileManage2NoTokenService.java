package com.citc.nce.filecenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.filecenter.entity.FileManage;
import com.citc.nce.filecenter.vo.UploadReq;
import com.citc.nce.filecenter.vo.UploadResp;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public interface FileManage2NoTokenService extends IService<FileManage> {

    ResponseEntity<byte[]> downloadScene(String uuid,String scene);

    List<UploadResp> uploadFile2Scene(UploadReq uploadReq);
}
