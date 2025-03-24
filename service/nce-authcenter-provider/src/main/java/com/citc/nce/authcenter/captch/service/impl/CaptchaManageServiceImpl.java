package com.citc.nce.authcenter.captch.service.impl;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.authcenter.captch.dao.CaptchaImageManageMapper;
import com.citc.nce.authcenter.captch.entity.CaptchaImageManageDo;
import com.citc.nce.authcenter.captch.service.CaptchaManageService;
import com.citc.nce.authcenter.captcha.vo.CaptchaImageInfo;
import com.citc.nce.authcenter.captcha.vo.req.CaptchaImageBatchInsertReq;
import com.citc.nce.authcenter.captcha.vo.req.CaptchaImageQueryListReq;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.filecenter.FileApi;
import com.citc.nce.filecenter.vo.DownloadReq;
import com.citc.nce.filecenter.vo.UploadForCaptchaImageReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.data.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CaptchaManageServiceImpl extends ServiceImpl<CaptchaImageManageMapper, CaptchaImageManageDo> implements IService<CaptchaImageManageDo>, CaptchaManageService {

    private final FileApi fileApi;

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchInsert(CaptchaImageBatchInsertReq req) {
        List<CaptchaImageManageDo> captchaImageManageDos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(req.getImages())) {
            String userId = SessionContextUtil.getUserId();
            LocalDateTime now = LocalDateTime.now();
            for (MultipartFile image : req.getImages()) {
                UploadForCaptchaImageReq uploadForCaptchaImageReq = new UploadForCaptchaImageReq();
                uploadForCaptchaImageReq.setFile(image);
                String fileId = fileApi.uploadForCaptcha(uploadForCaptchaImageReq);
                if (fileId != null) {
                    CaptchaImageManageDo captchaImageManageDo = new CaptchaImageManageDo();
                    captchaImageManageDo.setFileId(fileId);
                    captchaImageManageDo.setCreateTime(now);
                    captchaImageManageDo.setCreator(userId);
                    captchaImageManageDos.add(captchaImageManageDo);
                }

            }
            this.saveBatch(captchaImageManageDos);
        }
    }

    @Override
    public PageResult<CaptchaImageInfo> queryList(CaptchaImageQueryListReq req) {
        Page<CaptchaImageManageDo> captchaImageManageDoPage = this.lambdaQuery()
                .eq(CaptchaImageManageDo::getDeleted, 0)
                .page(new Page<>(req.getPageNo(), req.getPageSize()))
                .addOrder(OrderItem.desc("create_time"));
        if (captchaImageManageDoPage.getTotal() != 0) {
            List<CaptchaImageInfo> captchaImageInfos = new ArrayList<>();
            List<CaptchaImageManageDo> records = captchaImageManageDoPage.getRecords();
            for (CaptchaImageManageDo item : records) {
                CaptchaImageInfo captchaImageInfo = new CaptchaImageInfo();
                captchaImageInfo.setId(item.getId());
                captchaImageInfo.setFileId(item.getFileId());
                captchaImageInfos.add(captchaImageInfo);
            }
            return new PageResult<>(captchaImageInfos, captchaImageManageDoPage.getTotal());
        } else {
            return new PageResult<>();
        }
    }

    @Override
    public void delete(Long id) {
        CaptchaImageManageDo captchaImageManageDo = checkCaptchaImage(id);
        long count = this.count();
        if(count > 1){
            this.removeById(captchaImageManageDo.getId());
        }else{
            throw new BizException("最后一张验证码图片，不能删除！");
        }

    }

    @Override
    public ResponseEntity<byte[]> imageQuery(Long id) {
        //检查图片是否是验证码图片
        CaptchaImageManageDo captchaImageManageDo = checkCaptchaImage(id);
        DownloadReq downloadReq = new DownloadReq();
        downloadReq.setFileUUID(captchaImageManageDo.getFileId());
        return fileApi.download(downloadReq);
    }

    @Override
    public List<String> queryAllImageInfoForCaptcha() {
        List<String> result = new ArrayList<>();
        List<CaptchaImageManageDo> list = this.lambdaQuery().eq(CaptchaImageManageDo::getDeleted, 0).list();
        if(!CollectionUtils.isEmpty(list)) {
            String url = getFileCenterUrl();
            if(StringUtils.hasLength(url)){
                String imageUrl ;
                for(CaptchaImageManageDo captchaImageManageDo : list) {
                    imageUrl = url + "/download2Scene?scene=H5&uuid="+captchaImageManageDo.getFileId();
                    log.info("-------------imageUrl:{}",imageUrl);
                    result.add(imageUrl);
                }
            }else{
                log.error("filecenter-service is url is null :{}",url);
            }
        }
        return result;
    }

    private String getFileCenterUrl() {
        // 将服务名转换为具体的URL
        String serviceName = "filecenter-service";
        ServiceInstance instance = loadBalancerClient.choose(serviceName);
        if (instance != null) {
            return instance.getUri().toString();
        }
        return null;
    }

    private CaptchaImageManageDo checkCaptchaImage(Long id) {
        CaptchaImageManageDo captchaImageManageDo = this.getById(id);
        if (captchaImageManageDo == null) {
            throw new BizException("验证码图片不存在！");
        }
        return captchaImageManageDo;
    }
}
