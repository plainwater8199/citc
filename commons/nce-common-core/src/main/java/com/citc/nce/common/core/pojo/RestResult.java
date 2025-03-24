package com.citc.nce.common.core.pojo;

import com.citc.nce.common.core.exception.ErrorCode;
import lombok.Data;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/5/19 10:19
 * @Version: 1.0
 * @Description:
 */
@Data
public class RestResult<T> {

    /**
     *
     **/
    public static final Integer SUCCESS_CODE = 0;

    /**
     *
     **/
    public static final Integer FAIL_CODE = -1;

    /**
     * 成功或者失败的code错误码
     **/
    private Integer code;

    /**
     * 成功时返回的数据，失败时返回具体的异常信息
     **/
    private T data;

    /**
     * 请求失败返回的提示信息，给前端进行页面展示的信息
     **/
    private String msg;

    /**
     * 服务器当前时间
     **/
    private Date currentTime;

    private String traceId;


    public RestResult() {
        traceId = TraceContext.traceId();
    }

    public RestResult(Integer code, String msg) {
        this();
        this.code = code;
        this.msg = msg;
        this.currentTime = new Date();
    }


    public RestResult(Integer code, T data, String msg) {
        this();
        this.code = code;
        this.data = data;
        this.msg = msg;
        this.currentTime = new Date();
    }

    public RestResult(Integer code, T data) {
        this();
        this.code = code;
        this.data = data;
        this.msg = msg;
        this.currentTime = new Date();
    }


    public static <T> RestResult<T> success(T t) {
        RestResult result = new RestResult(0, t);
        return result;
    }

    public static <T> RestResult<T> success() {
        RestResult result = new RestResult(0, "操作成功！");
        return result;
    }

    public static <T> RestResult<T> error(ErrorCode errorCode) {
        return error(errorCode.getCode(), errorCode.getMsg());
    }

    public static <T> RestResult<T> error(Integer code, String msg) {
        RestResult result = new RestResult(code, msg);
        return result;
    }

    public static <T> RestResult<T> error(Integer code, String msg, T data) {
        RestResult result = new RestResult(code, msg);
        result.setData(data);
        return result;
    }

    public static boolean isSuccessOk(Integer pcode) {
        return SUCCESS_CODE.equals(pcode);
    }

    /*
    public void addResult(Object responseVO){
        this.data = (T) responseVO;
        Class<?> c = responseVO.getClass();
        Method getMethod = null;
        CommonResult commonResult = null;
        try {
            Class[] classArr = null;
            getMethod = c.getMethod("getCommonResult",classArr);
        }catch ( SecurityException  | NoSuchMethodException e){
            e.printStackTrace();
        }
        if(getMethod != null){
            Object[] objectArr = null;
            try {
                commonResult = (CommonResult) getMethod.invoke(responseVO,objectArr);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        if(commonResult != null){
            this.code = commonResult.isSuccess() ? SUCCESS_CODE : FAIL_CODE;
            this.msg = commonResult.getMsg();
        }
    }
*/
    @Override
    public String toString() {
        return "RestResult{" +
                ", code='" + code + '\'' +
                ", data=" + data +
                ", msg=" + msg +
                ", currentTime=" + currentTime +
                '}';
    }
}
