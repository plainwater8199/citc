package com.citc.nce.readingLetter.req;

import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.robot.vo.RobotGroupSendPlansAndChatbotAccount;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 文件名:ReadingLetterParseRecordReq
 * 创建者:zhujinyu
 * 创建时间:2024/7/17 17:09
 * 描述: 收到的短链解析回执
 */
@Data
public class FifthReadingLetterPlanAndChatbotAccountResp {

    private List<AccountManagementResp> chatbots;

    private List<RobotGroupSendPlansAndChatbotAccount> plans;
}
