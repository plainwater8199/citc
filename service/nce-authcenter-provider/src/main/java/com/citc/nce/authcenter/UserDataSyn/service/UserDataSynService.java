package com.citc.nce.authcenter.UserDataSyn.service;

import com.citc.nce.authcenter.userDataSyn.vo.UserDataSynReq;
import com.citc.nce.authcenter.userDataSyn.vo.UserDataSynResp;

public interface UserDataSynService {
    UserDataSynResp userDataSyn(UserDataSynReq req);

}
