
package com.citc.nce.rebot.files;

import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementOptionVo;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementTreeResp;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.facadeserver.annotations.SkipToken;
import com.citc.nce.configure.BaiduSensitiveCheckConfigure;
import com.citc.nce.dto.*;
import com.citc.nce.fileApi.*;
import com.citc.nce.filecenter.FileApi;
import com.citc.nce.filecenter.vo.*;
import com.citc.nce.rebot.util.FileUtil;
import com.citc.nce.vo.*;
import com.google.common.base.Strings;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * @Author: lidingyi
 * @Date: 2022/6/24 14:14
 * @Version: 1.0
 * @Description:
 */
@Api(value = "order", tags = "素材管理接口")
@RestController
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(BaiduSensitiveCheckConfigure.class)
public class RebotFilesController {

    @Resource
    private FileApi fileApi;

    @Resource
    private PictureApi pictureApi;

    @Resource
    private VideoApi videoApi;

    @Resource
    private UpFileApi upFileApi;

    @Resource
    private AudioApi audioApi;

    @Resource
    private GroupApi groupApi;

    @Resource
    private PlatformApi platformApi;

    @Resource
    private AccountManagementApi accountManagementApi;


    @ApiOperation("素材统计接口")
    @PostMapping({"/material/statistics"})
    public List<StatisticsResp> statistics(@RequestBody FileTidReq fileTidReq){
        return platformApi.statistics(fileTidReq);
    }

    @PostMapping("/material/used")
    @ApiOperation(value = "素材是否被已使用模板或已发布流程引用")
    public Boolean pictureUsed(@RequestBody  FileReq fileReq) {
        return platformApi.fileUsed(fileReq);
    }
    @PostMapping("/material/usedByAll")
    @ApiOperation(value = "素材是否被引用")
    public Boolean fileUsedAllPossible(@RequestBody  FileReq fileReq) {
        return platformApi.fileUsedAllPossible(fileReq);
    }
    @PostMapping("/material/verification")
    @ApiOperation("文件是否审核通过校验")
    public Boolean verification(@RequestBody VerificationReq verificationReq){
        return platformApi.verification(verificationReq);
    }

    @PostMapping("/file/examine")
    @ApiOperation("文件送审")
    public List<UploadResp> verification(@RequestBody ExamineReq examineReq){
        return fileApi.examine(examineReq);
    }


    @SkipToken
    @PostMapping("/{chatbotId}/delivery/mediaStatus")
    @ApiOperation("送审回调接口")
    public void mediaStatus(@RequestBody FileAccept request, @PathVariable("chatbotId") String chatbotId) {
        platformApi.mediaStatus(request, chatbotId);
    }

    @SkipToken
    @PostMapping("/delivery/mediaStatus")
    @ApiOperation("素材送审回调接口")
    public void mediaStatus(@RequestBody FileAccept request){
        platformApi.mediaStatus(request);
    }

    @ApiOperation("获取账户列表")
    @GetMapping("/material/account/list")
    public List<AccountResp> getAccountList() {
        List<AccountResp> accountResps = new ArrayList<>();
        List<AccountManagementOptionVo> allAccountManagement = accountManagementApi.getAllAccountManagement(true);
        for (AccountManagementOptionVo optionVo : allAccountManagement) {
            for (AccountManagementTreeResp option : optionVo.getOptions()) {
                AccountResp accountResp = new AccountResp();
                accountResp.setChatbotAccount(option.getChatbotAccount());
                accountResp.setAccountId(option.getChatbotAccountId());
                accountResp.setChatbotName(option.getAccountName());
                accountResp.setOperator(optionVo.getId());
                accountResp.setSupplierTag(option.getSupplierTag());
                accountResp.setChatbotStatus(option.getChatbotStatus());
                accountResps.add(accountResp);
            }
        }
        return accountResps;
    }

    @ApiOperation("账户空间使用情况")
    @GetMapping("/file/account/details")
    public List<AccountDetails> getDetails() {
        return fileApi.getDetails();
    }


    @ApiOperation("文件下载")
    @PostMapping("/file/download")
    public ResponseEntity<byte[]> download(@RequestBody @Valid DownloadReq downloadReq) {
        return fileApi.download(downloadReq);
    }

    @GetMapping(value = "/file/download/id")
    public ResponseEntity<byte[]> download2(@RequestParam String req, HttpServletRequest request) {
        String range = request.getHeader("Range");
        log.error("=========Range============:" + range);
        if (!Strings.isNullOrEmpty(req)) {
            //1、获取视频流文件
            FileInfoReq fileInfoReq = new FileInfoReq();
            fileInfoReq.setUuid(req);
            FileInfo fileInfo = fileApi.getFileInfo(fileInfoReq);
            List<String> videoFormatList = FileUtil.getVideoFormatList();
            //视频文件处理
            if (videoFormatList.contains(fileInfo.getFileFormat().toLowerCase(Locale.ROOT)) && !Strings.isNullOrEmpty(range)) {
                DownloadReq downloadReq = new DownloadReq();
                downloadReq.setFileUUID(req);
                downloadReq.setRange(range);
                return fileApi.videoDownload(downloadReq);
            } else {//其它文件处理
                DownloadReq downloadReq = new DownloadReq();
                downloadReq.setFileUUID(req);
                return fileApi.download(downloadReq);
            }
        } else {
            throw new BizException(GlobalErrorCode.BAD_REQUEST);
        }
    }

    @SkipToken
    @GetMapping(value = "/downloadFile")
    @ApiOperation("移动运营商下载文件接口")
    public ResponseEntity<byte[]> download3(@RequestParam("url") String url) {
        DownloadReq downloadReq = new DownloadReq();
        downloadReq.setFileUUID(url);
        return fileApi.download3(url);
    }

//    public ResponseEntity<byte[]> download2(@RequestParam String req) {
//        DownloadReq downloadReq = new DownloadReq();
//        downloadReq.setFileUUID(req);
//        return fileApi.download(downloadReq);
//    }

    @ApiOperation("文件删除")
    @PostMapping(value = "/file/delete")
    public void deleteFile(@RequestBody @Valid DeleteReq req) {
        fileApi.deleteFile(req);
    }

    @ApiOperation("图片保存")
    @PostMapping({"/material/picture/save"})
    public void savePicture(@RequestBody @Valid PictureReq req) {
        pictureApi.savePicture(req);
    }

    @ApiOperation("图片分页查询")
    @PostMapping({"/material/picture/list"})
    public PageResultResp<PictureResp> selectByPage(@RequestBody @Valid PageReq req) {
        return pictureApi.selectByPage(req);
    }

    @ApiOperation("图片删除")
    @PostMapping({"/material/picture/delete"})
    public DeleteResp deletePicture(@RequestBody @Valid MoveGroupReq req) {
        return pictureApi.deletePicture(req);
    }

    @ApiOperation("图片分组")
    @PostMapping({"/material/picture/move"})
    public void movePicture(@RequestBody @Valid MoveGroupReq req) {
        pictureApi.movePicture(req);
    }

//    @ApiOperation("根据分组id查询图片信息")
//    @PostMapping({"/material/picture/listById"})
//    public PageResultResp<PictureResp> findByGroupId(@RequestBody @Valid PageReq req){
//        return pictureApi.findByGroupId(req);
//    }

    @ApiOperation("音频保存")
    @PostMapping(value = "/material/audio/save")
    public void saveAudio(@RequestBody @Valid AudioReq audioReq) {
        audioApi.saveAudio(audioReq);
    }

    @ApiOperation("音频分页查询")
    @PostMapping(value = "/material/audio/list")
    public PageResultResp<AudioResp> selectAllAudios(@RequestBody @Valid PageReq pageReq) {
        return audioApi.selectAllAudios(pageReq);
    }

    @ApiOperation("音频删除")
    @PostMapping(value = "/material/audio/delete")
    public DeleteResp manageAudio(@RequestBody @Valid IdReq req) {
        return audioApi.deleteAudio(req);
    }

    @ApiOperation("分组保存")
    @PostMapping(value = "/material/group/save")
    public void saveGroup(@RequestBody @Valid GroupNameReq req) {
        groupApi.saveGroup(req);
    }

    @ApiOperation("分组分页查询")
    @GetMapping(value = "/material/group/list")
    public List<GroupResp> selectAllGroups() {
        return groupApi.selectAllGroups();
    }

    @ApiOperation("分组管理")
    @PostMapping(value = "/material/group/manage")
    public List<GroupResp> manageGroup(@RequestBody PageReq pageReq) {
        return groupApi.manageGroup(pageReq);
    }

    @ApiOperation("分组更新")
    @PostMapping(value = "/material/group/update")
    public void updateGroup(@RequestBody @Valid List<GroupReq> vos) {
        groupApi.updateGroup(vos);
    }

    @ApiOperation("文件保存")
    @PostMapping(value = "/material/file/save")
    public void saveUpFile(@RequestBody @Valid UpFileReq upFileDto) {
        upFileApi.saveUpFile(upFileDto);
    }

    @ApiOperation("文件分页查询")
    @PostMapping(value = "/material/file/list")
    public PageResultResp<UpFileResp> selectAll(@RequestBody @Valid PageReq req) {
        return upFileApi.selectAll(req);
    }

    @ApiOperation("文件删除")
    @PostMapping(value = "/material/file/delete")
    public DeleteResp deleteUpFile(@RequestBody @Valid IdReq req) {
        return upFileApi.deleteUpFile(req);
    }

    @ApiOperation("视频保存")
    @PostMapping(value = "/material/video/save")
    public void saveVideo(@RequestBody @Valid VideoReq req) {
        videoApi.saveVideo(req);
    }

    @ApiOperation("根据id查找单个视频")
    @PostMapping(value = "/material/video/findOne")
    public VideoResp findOne(@RequestBody @Valid IdReq req) {
        return videoApi.findOne(req);
    }

    @ApiOperation("视频分页查询")
    @PostMapping(value = "/material/video/list")
    public PageResultResp<VideoResp> selectAllVideos(@RequestBody @Valid PageReq req) {
        return videoApi.selectAllVideos(req);
    }

    @ApiOperation("视频更新")
    @PostMapping(value = "/material/video/update")
    public void updateVideo(@RequestBody @Valid VideoReq req) {
        videoApi.updateVideo(req);
    }

    @ApiOperation("视频删除")
    @PostMapping(value = "/material/video/delete")
    public DeleteResp deleteVideo(@RequestBody @Valid IdReq req) {
        return videoApi.deleteVideo(req);
    }
}
