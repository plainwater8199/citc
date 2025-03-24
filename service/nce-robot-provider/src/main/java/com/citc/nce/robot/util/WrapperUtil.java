package com.citc.nce.robot.util;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class WrapperUtil {
    public static QueryWrapper entity2Wrapper(Object obj) {
        QueryWrapper wrapper = new QueryWrapper();
        if (obj == null){
            return wrapper;
        }
        Class<?> aClass = obj.getClass();
        Field[] fields = obj.getClass().getDeclaredFields();
        //遍历属性
        for (Field field : fields) {
            Method method = null;
            try {
                String fieldName = field.getName();
                //跳过serialVersionUID
                if (fieldName.equals("serialVersionUID")) {
                    continue;
                }
                //获取属性上的注解
                TableField fieldAnnotation = field.getAnnotation(TableField.class);
                TableId idAnnotation = field.getAnnotation(TableId.class);
                //拿到列名
                String value = fieldAnnotation == null ? idAnnotation.value() : fieldAnnotation.value();
                //get方法
                method = aClass.getDeclaredMethod("get" + captureName(fieldName), null);
                Object returnValue = method.invoke(obj);
                if (returnValue instanceof String) {
                    String str = (String) returnValue;
                    wrapper.eq(!StringUtils.hasText(str), value, returnValue);
                } else {
                    wrapper.eq(returnValue != null, value, returnValue);
                }

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return wrapper;
    }
    /**
     * 将字符串的首字母转大写
     *
     * @param str 需要转换的字符串
     * @return
     */
    private static String captureName(String str) {
        // 进行字母的ascii编码前移，效率要高于截取字符串进行转换的操作
        char[] cs = str.toCharArray();
        cs[0] -= 32;
        return String.valueOf(cs);
    }
}
