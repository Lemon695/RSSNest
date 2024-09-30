package com.rss.nest.enums;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Lemon695
 * @date: 2024/9/30 下午5:54:17
 * @description:
 */
@Getter
public enum EnumResultCode {

    SUCCESS(200, "success"),

    EXCEPTION(9999, "exception"),

    ;
    private final Integer code;
    private final String desc;

    EnumResultCode(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static EnumResultCode getErrorByCode(Integer code) {
        for (EnumResultCode eec : EnumResultCode.values()) {
            if (code.equals(eec.getCode())) {
                return eec;
            }
        }
        return null;
    }
}
