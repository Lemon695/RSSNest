package com.rss.nest.models.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rss.nest.enums.EnumResultCode;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Lemon695
 * @date: 2024/9/30 下午5:53:17
 * @description:
 */
@Data
public class Response<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应Code
     */
    private Integer code;

    /**
     * 响应结果
     */
    private String msg;

    /**
     * 响应数据
     */
    @JsonProperty("data")
    private T data;

    /**
     * 内部参数-是否打印错误日志
     */
    @JsonIgnore
    private boolean logError = true;

    /**
     * 来源类型
     */
    @JsonIgnore
    private String sourceCode;

    public Response() {
    }

    public Response(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static <T> Response<T> initSuccess() {
        Response<T> response = new Response<>();
        response.setCode(EnumResultCode.SUCCESS.getCode());
        response.setMsg(EnumResultCode.SUCCESS.getDesc());
        response.setData(null);
        return response;
    }

    /**
     * 成功响应
     *
     * @param <T> 通用参数
     * @param t   通用类
     */
    public static <T> Response<T> initSuccess(T t) {
        Response<T> response = new Response<>();
        response.setCode(EnumResultCode.SUCCESS.getCode());
        response.setMsg(EnumResultCode.SUCCESS.getDesc());
        response.setData(t);
        return response;
    }

    public static <T> Response<T> initSuccess(Class<T> clazz) {
        Response<T> response = new Response<>();
        response.setCode(EnumResultCode.SUCCESS.getCode());
        response.setMsg(EnumResultCode.SUCCESS.getDesc());
        // 根据传入的类参数进行相应的处理，这里假设设置一个默认值
        try {
            // 默认实例化对象
            T data = clazz.newInstance();
            response.setData(data);
        } catch (InstantiationException | IllegalAccessException e) {
            // 这里可以根据具体情况处理异常
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 异常响应
     *
     * @param resultCode 错误码
     * @param msg        错误信息
     * @param <T>        通用参数
     */
    public static <T> Response<T> initError(EnumResultCode resultCode, String msg) {
        Response<T> response = new Response<>();
        response.setCode(resultCode.getCode());
        response.setMsg(msg);

        return response;
    }

    public static <T> Response<T> initError(EnumResultCode resultCode) {
        Response<T> response = new Response<>();
        response.setCode(resultCode.getCode());
        response.setMsg(resultCode.getDesc());

        return response;
    }

    /**
     * 系统异常错误（未拦截错误）
     *
     * @param <T> 通用参数
     */
    public static <T> Response<T> initSystemException() {
        Response<T> response = new Response<>();
        response.setCode(EnumResultCode.EXCEPTION.getCode());
        response.setMsg(EnumResultCode.EXCEPTION.getDesc());

        return response;
    }
}
