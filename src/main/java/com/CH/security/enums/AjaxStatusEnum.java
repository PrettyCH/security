package com.CH.security.enums;

import lombok.Getter;

@Getter
public enum AjaxStatusEnum {
    SUCCESS(0,"成功"),ERROR(1,"失败"),NOLOGIN(2,"未登录");
    private Integer code;
    private String name;

    private AjaxStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static AjaxStatusEnum find(Integer code) {
        for (AjaxStatusEnum instance : AjaxStatusEnum.values()) {
            if (instance.getCode().equals(code)) {
                return instance;
            }
        }
        return null;
    }

    public static String getNameByCode(Integer code) {
        AjaxStatusEnum status = find(code);
        return null == status ? "" : status.getName();
    }
}
