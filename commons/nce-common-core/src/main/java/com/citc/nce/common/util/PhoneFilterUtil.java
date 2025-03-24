package com.citc.nce.common.util;

import cn.hutool.core.collection.CollectionUtil;
import com.citc.nce.common.constants.CarrierEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 文件名:PhoneFilterUtil
 * 创建者:zhujinyu
 * 创建时间:2024/8/6 18:03
 * 描述:
 */
@Slf4j
public class PhoneFilterUtil {

    //过滤掉不属于指定号段的手机号码
    public static List<String> filterSelectedPhones(List<String> sendPhones, String selectedCarriers, Map<String, String> allSegments) {
        //数组变为集合
        List<Integer> selectedCarriersList = Arrays.stream(selectedCarriers.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        String unicomSegment = allSegments.get(CarrierEnum.Unicom.getName());
        String mobileSegment = allSegments.get(CarrierEnum.CMCC.getName());
        String telecomSegment = allSegments.get(CarrierEnum.Telecom.getName());
        Set<String> unicomSegmentList = Arrays.stream(unicomSegment.split(",")).collect(Collectors.toSet());
        Set<String> mobileSegmentList = Arrays.stream(mobileSegment.split(",")).collect(Collectors.toSet());
        Set<String> telecomSegmentList = Arrays.stream(telecomSegment.split(",")).collect(Collectors.toSet());

        for (int i = sendPhones.size() - 1; i >= 0; i--) {
            String phone = sendPhones.get(i);
            boolean delete = true;
            //联通
            if (selectedCarriersList.contains(1)) {
                if (unicomSegmentList.contains(phone.substring(0, 3))) {
                    delete = false;
                }
            }
            //移动
            if (selectedCarriersList.contains(2)) {
                if (mobileSegmentList.contains(phone.substring(0, 3))) {
                    delete = false;
                }
            }
            //电信
            if (selectedCarriersList.contains(3)) {
                if (telecomSegmentList.contains(phone.substring(0, 3))) {
                    delete = false;
                }
            }
            if (delete) {
                sendPhones.remove(i);
                log.info("删除号码：{}", phone);
            }
        }
        return sendPhones;
    }

    //过滤掉不属于指定号段的手机号码
    public static List<String> createSelectedPhones(List<String> sendPhones, String selectedCarriers, Map<String, String> allSegments) {
        List<String> result = CollectionUtil.newArrayList(sendPhones);
        //数组变为集合
        List<Integer> selectedCarriersList = Arrays.stream(selectedCarriers.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        //如果是硬核桃就直接全部返回
        if (selectedCarriersList.contains(0)) {
            return sendPhones;
        }

        String unicomSegment = allSegments.get(CarrierEnum.Unicom.getName());
        String mobileSegment = allSegments.get(CarrierEnum.CMCC.getName());
        String telecomSegment = allSegments.get(CarrierEnum.Telecom.getName());
        Set<String> unicomSegmentList = Arrays.stream(unicomSegment.split(",")).collect(Collectors.toSet());
        Set<String> mobileSegmentList = Arrays.stream(mobileSegment.split(",")).collect(Collectors.toSet());
        Set<String> telecomSegmentList = Arrays.stream(telecomSegment.split(",")).collect(Collectors.toSet());

        for (int i = result.size() - 1; i >= 0; i--) {
            String phone = result.get(i);
            boolean delete = true;
            //联通
            if (selectedCarriersList.contains(1)) {
                if (unicomSegmentList.contains(phone.substring(0, 3))) {
                    delete = false;
                }
            }
            //移动
            if (selectedCarriersList.contains(2)) {
                if (mobileSegmentList.contains(phone.substring(0, 3))) {
                    delete = false;
                }
            }
            //电信
            if (selectedCarriersList.contains(3)) {
                if (telecomSegmentList.contains(phone.substring(0, 3))) {
                    delete = false;
                }
            }
            if (delete) {
                result.remove(i);
                log.info("删除号码：{}", phone);
            }
        }
        return result;
    }


    //过滤掉不属于指定号段的手机号码
    public static Map<Integer, Integer> countPhonesOfOperator(List<String> sendPhones, Map<String, String> allSegments) {
        //数组变为集合
        HashMap<Integer, Integer> result = new HashMap<>();
        String unicomSegment = allSegments.get(CarrierEnum.Unicom.getName());
        String mobileSegment = allSegments.get(CarrierEnum.CMCC.getName());
        String telecomSegment = allSegments.get(CarrierEnum.Telecom.getName());
        Set<String> unicomSegmentList = Arrays.stream(unicomSegment.split(",")).collect(Collectors.toSet());
        Set<String> mobileSegmentList = Arrays.stream(mobileSegment.split(",")).collect(Collectors.toSet());
        Set<String> telecomSegmentList = Arrays.stream(telecomSegment.split(",")).collect(Collectors.toSet());
        int unicomNumber = 0;
        int mobileNumber = 0;
        int telecomNumber = 0;
        for (int i = sendPhones.size() - 1; i >= 0; i--) {
            String phone = sendPhones.get(i);
            //联通
            if (unicomSegmentList.contains(phone.substring(0, 3))) {
                unicomNumber++;
            }
            //移动
            else if (mobileSegmentList.contains(phone.substring(0, 3))) {
                mobileNumber++;
            }
            //电信
            else if (telecomSegmentList.contains(phone.substring(0, 3))) {
                telecomNumber++;
            }
        }
        result.put(CarrierEnum.Unicom.getValue(), unicomNumber);
        result.put(CarrierEnum.CMCC.getValue(), mobileNumber);
        result.put(CarrierEnum.Telecom.getValue(), telecomNumber);
        return result;
    }
}
