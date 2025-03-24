package com.citc.nce.common.facadeserver.verifyCaptcha.Aspect;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.application.vo.CaptchaResponse;
import cloud.tianai.captcha.application.vo.ImageCaptchaVO;
import cloud.tianai.captcha.common.response.ApiResponse;
import cloud.tianai.captcha.spring.plugins.secondary.SecondaryVerificationApplication;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import com.alibaba.fastjson.JSON;
import com.citc.nce.common.bean.CheckData;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

/**
 *
 * @author bydud
 * @since 2024/4/1
 */
@Aspect
@Component
@Slf4j
public class VerifyCaptchaAspectImpl {
    @Autowired
    private ImageCaptchaApplication imageCaptchaApplication;

    //生成验证码
    public CaptchaResponse<ImageCaptchaVO> generateCaptcha(String type) {
        return imageCaptchaApplication.generateCaptcha(type);
    }

    //验证验证码
    public ApiResponse<?> matching(CheckData data) {
        com.citc.nce.common.bean.ImageCaptchaTrack  imageCaptchaTrack= data.getData();
        String trackJson = JSON.toJSONString(imageCaptchaTrack.getTrackList());
        ImageCaptchaTrack track = new ImageCaptchaTrack();
        track.setBgImageWidth(imageCaptchaTrack.getBgImageWidth());
        track.setBgImageHeight(imageCaptchaTrack.getBgImageHeight());
        track.setStartTime(imageCaptchaTrack.getStartSlidingTime());
        track.setStopTime(imageCaptchaTrack.getEndSlidingTime());
        track.setTemplateImageHeight(imageCaptchaTrack.getSliderImageHeight());
        track.setTemplateImageWidth(imageCaptchaTrack.getSliderImageWidth());
        track.setTrackList(JSON.parseArray(trackJson, ImageCaptchaTrack.Track.class));
        return imageCaptchaApplication.matching(data.getId(), track);
    }


    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @Around("@annotation(com.citc.nce.common.facadeserver.verifyCaptcha.VerifyCaptchaV2)")
    public Object doAfterReturningV2(ProceedingJoinPoint joinPoint) throws Throwable {
        boolean flag = false;
        if (imageCaptchaApplication instanceof SecondaryVerificationApplication) {
            String check2Id = getCheck2Id(joinPoint);
            flag = ((SecondaryVerificationApplication) imageCaptchaApplication).secondaryVerification(check2Id);
        }
        if (flag) {
            return joinPoint.proceed();
        } else {
            throw new VerifyCaptchaExp();
        }
    }

    @Around("@annotation(com.citc.nce.common.facadeserver.verifyCaptcha.VerifyCaptcha)")
    public Object doAfterReturning(ProceedingJoinPoint joinPoint) throws Throwable {
        ApiResponse<?> matching = matching(getCheckData(joinPoint));
        if (matching.isSuccess()) {
            return joinPoint.proceed();
        } else {
            throw new VerifyCaptchaExp(matching.getMsg());
        }
    }

    /**
     * 获取参数
     * @return
     */
    private String getCheck2Id(ProceedingJoinPoint joinPoint) {
        String checkData = null;
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            try {
                Class<?> aClass = arg.getClass();
                checkData = aClass.getDeclaredMethod("getRotateCheck2Id").invoke(arg).toString();
            } catch (IllegalAccessException |
                     NoSuchMethodException | InvocationTargetException e) {
                log.error("验证码参数获取失败");
            }
        }
        if (Objects.nonNull(checkData)) {
            return checkData;
        }
        throw new RuntimeException("验证码参数不正确");
    }

    private static final String GET = "get";

    /**
     * 获取参数
     * @return
     */
    private CheckData getCheckData(ProceedingJoinPoint joinPoint) {
        CheckData checkData = null;
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            try {
                Class<?> aClass = arg.getClass();
                for (Field field : aClass.getDeclaredFields()) {
                    if (CheckData.class.equals(field.getType())) {
                        checkData = (CheckData) aClass.getDeclaredMethod(GET + capitalizeFirstLetter(field.getName())).invoke(arg);
                    }
                }
            } catch (IllegalAccessException |
                     NoSuchMethodException | InvocationTargetException e) {
                log.error("验证码参数获取失败");
            }
        }

        if (Objects.nonNull(checkData)) {
            return checkData;
        }
        throw new RuntimeException("验证码参数不正确");
    }


    private static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        StringBuilder sb = new StringBuilder();
        String[] words = input.split("\\s+");
        for (String word : words) {
            sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
        }
        return sb.toString().trim();
    }

}
