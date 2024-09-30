package com.rss.nest.exception;

import com.rss.nest.enums.EnumResultCode;
import com.rss.nest.models.common.Response;
import lombok.Getter;

/**
 * @author Lemon695
 * @date: 2024/9/30 下午5:02:17
 * @description:
 */
@Getter
public class RSSNestException extends Exception {

    private static final long serialVersionUID = 1L;

    private final Response response;

    public RSSNestException(Response response) {
        this.response = response;
    }

    public RSSNestException(EnumResultCode resultCode) {
        this.response = new Response<>();
        response.setCode(resultCode.getCode());
        response.setMsg(resultCode.getDesc());
    }

    public RSSNestException(EnumResultCode resultCode, boolean logError) {
        this.response = new Response<>();
        response.setCode(resultCode.getCode());
        response.setMsg(resultCode.getDesc());
        response.setLogError(logError);
    }

    public RSSNestException(EnumResultCode resultCode, String msg) {
        this.response = new Response<>();
        response.setCode(resultCode.getCode());
        response.setMsg(msg);
    }

}
