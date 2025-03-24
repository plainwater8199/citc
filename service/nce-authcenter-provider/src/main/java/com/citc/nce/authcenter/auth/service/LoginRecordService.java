package com.citc.nce.authcenter.auth.service;

import com.citc.nce.authcenter.user.entity.UserDo;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author yy
 * @date 2024-08-02 09:25:53
 */

public interface LoginRecordService {
     void insertLoginRecord(UserDo user, Map<String, Integer> version, Integer platformType) ;
    }
