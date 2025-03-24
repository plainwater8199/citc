package com.citc.nce.robot.controller;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import com.citc.nce.robot.RobotVariableApi;
import com.citc.nce.robot.bean.RobotVariableBean;
import com.citc.nce.robot.config.SysteSettingConstants;
import com.citc.nce.robot.dao.RobotVariableDao;
import com.citc.nce.robot.entity.RobotVariableDo;
import com.citc.nce.robot.exception.RobotErrorCode;
import com.citc.nce.robot.service.RobotVariableService;
import com.citc.nce.robot.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;


/**
 * @Author: weilanglang
 * @Contact: llweix
 * @Date: 2022/6/30 17:11
 * @Version: 1.0
 * @Description:
 */
@RestController
@RequestMapping("/robot")
public class RobotVariableController implements RobotVariableApi {


    @Resource()
    RobotVariableService variableService;
    @Resource
    RobotVariableDao robotVariableDao;

    /**
     *  变量添加
     * */
    @PostMapping("/variable/save")
    public void save(@RequestBody RobotVariableReq robotVariableReq) {
        String variableName = robotVariableReq.getVariableName();
        if (StrUtil.isBlank(variableName)){
            throw new BizException(RobotErrorCode.VARIABLE_BAD_REQUEST);
        }
        BaseUser user = SessionContextUtil.getUser();

//        if (ObjectUtil.isNull(user)){
//            throw new BizException(GlobalErrorCode.NOT_LOGIN_ERROR);
//        }
        //保存时 查看变量名称是否已经存在
        boolean systemSetting = isSystemSetting(variableName);
        if (systemSetting){
            //为系统变量，保存失败
            throw new BizException(RobotErrorCode.VARIABLE_SYSTEM_DATA_EXIST);
        }
        RobotVariableResp result = variableService.listByName(variableName,user);
        if (ObjectUtil.isNotNull(result)&&result.getList().size()!=0){
            //改变量名称已经存在
            throw new BizException(RobotErrorCode.VARIABLE_DATA_EXIST);
        }
        variableService.saveVariable(robotVariableReq,user);
    }


    /**
     * 变量列表查询
     * */
    @PostMapping("/variable/list")
    public RobotVariableResp listAll(@RequestBody RobotVariablePageReq robotVariablePageReq) {
        return variableService.list(robotVariablePageReq);
    }

    @Override
    @PostMapping("/variable/queryByTsOrderId")
    public List<RobotVariableBean> queryByTsOrderId(@RequestBody RobotVariableReq req) {
        return variableService.queryByTsOrderId(req.getTsOrderId());
    }

    /**
     * 变量列表查询
     * */
    @PostMapping("/variable/queryById")
    public RobotVariableBean queryById(RobotVariableReq robotVariableReq) {
        BaseUser user = SessionContextUtil.getUser();

//        if (ObjectUtil.isNull(user)){
//            throw new BizException(GlobalErrorCode.NOT_LOGIN_ERROR);
//        }
        RobotVariableBean robotVariableBean = variableService.queryById(robotVariableReq);
        return robotVariableBean;
    }

    @PostMapping("/variable/getList")
    @Override
    public RobotVariableResp getList(RobotVariableCreateReq robotVariableCreateReq) {
        RobotVariableResp variableResp = variableService.getList(robotVariableCreateReq);
        return variableResp;
    }

    @PostMapping("/variable/editByName")
    public RobotValueResetResp editByName(@RequestBody RobotVariableReq robotVariableReq){
        String code = "0";
        try {
            BaseUser user = SessionContextUtil.getUser();
            if (null == user) {
                user = new BaseUser();
                user.setUserId(robotVariableReq.getCreator());
            }
            LambdaQueryWrapperX<RobotVariableDo> queryWrapper = new LambdaQueryWrapperX<>();
            queryWrapper.eq(RobotVariableDo::getVariableName, robotVariableReq.getVariableName())
                            .eq(RobotVariableDo::getCreator, user.getUserId())
                                    .eq(RobotVariableDo::getDeleted, 0);
            List<RobotVariableDo> variableDos = robotVariableDao.selectList(queryWrapper);
            RobotVariableDo variableDo = variableDos.get(0);
            if (StringUtils.isNotEmpty(robotVariableReq.getVariableValue())) {
                variableDo.setVariableValue(robotVariableReq.getVariableValue());
                robotVariableDao.updateById(variableDo);
            }
        } catch (Exception e) {
            code = "1";
        }
        RobotValueResetResp robotValueResetResp = new RobotValueResetResp();
        robotValueResetResp.setCode(code);
        return robotValueResetResp;
    }
    /**
     * 变量编辑
     * */
    @PostMapping("/variable/edit")
    public void compile(@RequestBody RobotVariableReq robotVariableReq) {
        long id = robotVariableReq.getId();
         String variableName = robotVariableReq.getVariableName();
        BaseUser user = SessionContextUtil.getUser();
        if (ObjectUtil.isNull(user)){
            throw new BizException(GlobalErrorCode.NOT_LOGIN_ERROR);
        }
        if (StrUtil.isBlank(variableName)){
            throw new BizException(RobotErrorCode.VARIABLE_BAD_REQUEST);
        }
        if(ObjectUtil.isNull(id)||id==0){
            throw new BizException(RobotErrorCode.VARIABLE_ID_VARIABLE_DATA_EXIST);
        }

        //编辑时 查看变量名称是否已经存在
        boolean systemSetting = isSystemSetting(variableName);
        if (systemSetting){
            //为系统变量，保存失败
            throw new BizException(RobotErrorCode.VARIABLE_SYSTEM_DATA_EXIST);
        }
        RobotVariableResp result = variableService.listByName(variableName,user);
        if (ObjectUtil.isNotNull(result)&& !result.getList().isEmpty()){
            List<RobotVariableBean> variableList = result.getList();
            for (RobotVariableBean variableBean : variableList){
                if (variableBean.getId()!=robotVariableReq.getId()){
                    //改变量名称已经存在
                    throw new BizException(RobotErrorCode.VARIABLE_DATA_EXIST);
                }
            }
        }
        variableService.editVariable(robotVariableReq);
    }
    /**
     * 变量删除
     * */
    @PostMapping("/variable/delete")
    public void removeVariable(@RequestBody RobotVariableReq robotVariableReq) {
        long id = robotVariableReq.getId();

        if (ObjectUtil.isNull(id)||id==0){
            throw new BizException(RobotErrorCode.VARIABLE_BAD_REQUEST);
        }
        BaseUser user = SessionContextUtil.getUser();
        variableService.removeVariable(id,user);
    }


    /**
     * 判断变量是否为系统变量
     * */

    public boolean isSystemSetting(String name){
        boolean isSystem=false;
        if (SysteSettingConstants.CURRENTTIME.equals(name)
        ||SysteSettingConstants.MOBILENO.equals(name)
        ||SysteSettingConstants.FROMPROCESS.equals(name)){
            isSystem=true;
        }
        return isSystem;
    }
}
