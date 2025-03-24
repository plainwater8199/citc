package com.citc.nce.robot.service.impl;

import com.alibaba.excel.EasyExcel;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.robot.exception.RobotErrorCode;
import com.citc.nce.robot.util.PhoneData;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ContactsService {


    private static final String regex = "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$";

    public Boolean checkContacts(MultipartFile file) {
        try {

            List<PhoneData> phoneDataList = EasyExcel.read(file.getInputStream()).sheet(1).head(PhoneData.class).doReadSync();
            AtomicBoolean result = new AtomicBoolean(true);
            phoneDataList.stream().map(PhoneData::getPhone).collect(Collectors.toList()).forEach(p -> {
                if (!Pattern.matches(regex, p)) {
                    result.set(false);
                }
            });
            return result.get();
        } catch (Exception e) {
            throw new BizException(RobotErrorCode.EXCEL_ERROR);
        }
    }
}
