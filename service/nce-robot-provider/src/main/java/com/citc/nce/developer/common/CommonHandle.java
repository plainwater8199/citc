package com.citc.nce.developer.common;

import com.citc.nce.developer.enums.SendResult;
import com.citc.nce.developer.vo.DeveloperCustomerVo;
import com.citc.nce.developer.vo.DeveloperSendSearchCommonVo;

/**
 * @author ping chen
 */
public class CommonHandle {
    public static DeveloperSendSearchCommonVo sendRequestHandle(SendResult sendResult){
        DeveloperSendSearchCommonVo developerSendSearchCommonVo = new DeveloperSendSearchCommonVo();
        if(sendResult != null){
            switch (sendResult){
                case CALL_FAIL:
                    developerSendSearchCommonVo.setCallResult(1);
                    break;
                case SEND_PLATFORM_FAIL:
                    developerSendSearchCommonVo.setSendPlatformResult(1);
                    break;
                case SEND_PLATFORM_ING://调用成功，并且没有发送平台结果。
                    developerSendSearchCommonVo.setCallResult(0);
                    developerSendSearchCommonVo.setSendPlatformResult(-1);//平台没有结果，is null
                    break;
                case SEND_PLATFORM_SUCCESS:
                    developerSendSearchCommonVo.setSendPlatformResult(0);
                    break;
                default:
                    break;
            }
        }
        return developerSendSearchCommonVo;
    }
}
