package com.CH.security.enums;

import com.wf.captcha.*;
import com.wf.captcha.base.Captcha;
import lombok.Getter;

/**
 * @author ch
 * @version 1.0
 * @date 2021/1/28 10:39
 * 图形验证码枚举类
 */
@Getter
public enum EasyCaptchaEnum {
    ARITHMETIC(0, ArithmeticCaptcha.class),
    CHINESE(1, ChineseCaptcha.class),
    CHINESEGIF(2, ChineseGifCaptcha.class),
    GIF(3,  GifCaptcha.class),
    SPEC(4,  SpecCaptcha.class);

    private Integer code;
    private Class<? extends  Captcha> className;

    private EasyCaptchaEnum(Integer code, Class className) {
        this.code = code;
        this.className = className;
    }

    public static EasyCaptchaEnum find(Integer code) {
        for (EasyCaptchaEnum instance : EasyCaptchaEnum.values()) {
            if (instance.getCode().equals(code)) {
                return instance;
            }
        }
        return null;
    }
    public static Class getNameByCode(Integer code) {
        EasyCaptchaEnum dateEnum = find(code);
        return dateEnum.getClassName();
    }
}
