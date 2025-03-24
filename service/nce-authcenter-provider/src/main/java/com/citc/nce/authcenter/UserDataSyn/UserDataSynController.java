package com.citc.nce.authcenter.UserDataSyn;

import com.citc.nce.authcenter.UserDataSyn.service.UserDataSynService;
import com.citc.nce.authcenter.admin.service.AdminUserService;
import com.citc.nce.authcenter.tenantdata.service.TenantDataSynService;
import com.citc.nce.authcenter.tenantdata.vo.DataSynReq;
import com.citc.nce.authcenter.userDataSyn.UserDataSynApi;
import com.citc.nce.authcenter.userDataSyn.vo.CommunityUserInfo;
import com.citc.nce.authcenter.userDataSyn.vo.UserDataSynReq;
import com.citc.nce.authcenter.userDataSyn.vo.UserDataSynResp;
import com.citc.nce.common.util.JsonUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.*;
import java.nio.charset.StandardCharsets;

@RestController()
@Slf4j
public class UserDataSynController implements UserDataSynApi {

    @Resource
    private UserDataSynService userDataSynService;

    @Resource
    private TenantDataSynService tenantDataSynService;
    @Resource
    private AdminUserService adminUserService;

    @Override
    @PostMapping(value = "/captcha/userDataSyn",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public UserDataSynResp userDataSyn(UserDataSynReq req) {
        return userDataSynService.userDataSyn(req);
    }


    @Override
    @ApiOperation("多租户数据同步")
    @PostMapping("/captcha/tenant/dataSyn")
    public Object dataSyn() {
        DataSynReq req = new DataSynReq();
        return tenantDataSynService.dataSyn(req);
    }

//    @Override
//    @ApiOperation("多租户数据统计基础数据同步")
//    @PostMapping("/captcha/tenant/signSyn")
//    public Object signSyn() {
//        DataSynReq req = new DataSynReq();
//        return tenantDataSynService.signSyn(req);
//    }


    @Override
    @ApiOperation("多租户数据统计基础数据同步")
    @PostMapping("/captcha/tenant/statisticBaseDataSyn")
    public Object statisticBaseDataSyn() {
        DataSynReq req = new DataSynReq();
        return tenantDataSynService.statisticBaseDataSyn(req);
    }

    @Override
    @ApiOperation("多租户数据统计同步")
    @PostMapping("/captcha/tenant/statisticDataSyn")
    public Object statisticDataSyn() {
        DataSynReq req = new DataSynReq();
        return tenantDataSynService.statisticDataSyn(req);
    }



    @ApiOperation("超级管理员权限同步")
    @GetMapping("/data/superAdminMenuSyn")
    public void superAdminMenuSyn(@RequestParam("adminRoleId") String adminRoleId) {
         adminUserService.superAdminMenuSyn(adminRoleId);
    }


    @ApiOperation("同步固定菜单chatbot账号")
    @PostMapping("/data/menuForChatbot")
    public void menuForChatbot() {
        tenantDataSynService.menuForChatbot();
    }















    static String fileAddress = "D:\\water\\userData123.json";

    @GetMapping(value = "/captcha/userDatas")
    public CommunityUserInfo userDatas() {
        CommunityUserInfo communityUserInfo = new CommunityUserInfo();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(fileAddress), StandardCharsets.UTF_8));
            StringBuilder stringBuilder = new StringBuilder("");
            while (bufferedReader.ready()) {
                stringBuilder.append(bufferedReader.readLine());
            }
            communityUserInfo = JsonUtils.string2Obj(stringBuilder.toString(), CommunityUserInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(bufferedReader != null){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return communityUserInfo;
    }
}
