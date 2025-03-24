//package com.citc.nce.im.controller;
//
//import com.baomidou.mybatisplus.core.toolkit.Wrappers;
//import com.citc.nce.im.massSegment.entity.MassSegment;
//import com.citc.nce.im.massSegment.mapper.MassSegmentMapper;
//import com.citc.nce.robot.api.MassSegmentApi;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.util.CollectionUtils;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
///**
// * @author jcrenc
// * @since 2024/2/21 10:43
// */
//@RestController
//@Slf4j
//@RequiredArgsConstructor
//public class MassSegmentController implements MassSegmentApi {
//    private final MassSegmentMapper massSegmentMapper;
//
//    @Override
//    public String queryOperator(String phoneSegment) {
//        if (phoneSegment.length() != 3)
//            return null;
//        List<MassSegment> massSegments = massSegmentMapper.selectList(
//                Wrappers.<MassSegment>lambdaQuery()
//                        .eq(MassSegment::getPhoneSegment, phoneSegment)
//        );
//        if (!CollectionUtils.isEmpty(massSegments)){
//            return massSegments.get(0).getOperator();
//        }
//        return null;
//    }
//
//    @Override
//    public Map<String, String> queryAllSegment() {
//        List<MassSegment> massSegments = massSegmentMapper.selectList(null);
//        return massSegments.stream().collect(Collectors.groupingBy(MassSegment::getOperator, Collectors.mapping(MassSegment::getPhoneSegment, Collectors.joining(","))));
//    }
//}
