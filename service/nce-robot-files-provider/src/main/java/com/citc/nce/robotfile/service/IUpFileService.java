package com.citc.nce.robotfile.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.dto.IdReq;
import com.citc.nce.dto.PageReq;
import com.citc.nce.dto.UpFileReq;
import com.citc.nce.robotfile.entity.UpFileDo;
import com.citc.nce.vo.DeleteResp;
import com.citc.nce.vo.PageResultResp;
import com.citc.nce.vo.UpFileResp;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年6月29日16:29:48
 * @Version: 1.0
 * @Description: IPictureService
 */
public interface IUpFileService extends IService<UpFileDo> {

    void saveUpFile(UpFileReq upFileDto);

    PageResultResp<UpFileResp> selectAll(PageReq req);

    DeleteResp deleteUpFile(IdReq req);
}
