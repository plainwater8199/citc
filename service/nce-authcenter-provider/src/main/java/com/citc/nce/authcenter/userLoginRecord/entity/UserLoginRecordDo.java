package com.citc.nce.authcenter.userLoginRecord.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/27 15:33
 */
@Data
@TableName("user_login_record")
public class UserLoginRecordDo extends BaseDo<UserLoginRecordDo> implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userId;

    private String cspAccount;

    private String name;

    private Date dailyFirstLoginTime;

    /**
     * 平台信息1:核能商城客户端 2:chatbot客户端 3:硬核桃社区 4:管理平台
     */
    private Integer platformType;
    /**
     * 法务文件版本号信息
     */
    private String versionInfo;


}
