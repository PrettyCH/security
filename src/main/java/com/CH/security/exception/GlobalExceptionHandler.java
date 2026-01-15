package com.CH.security.exception;

import com.CH.security.utils.RespBean;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author ch
 * @version 1.0
 * @date 2021/1/28 16:09
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(NoLoginException.class)
    public RespBean<String> noLoginHandle(NoLoginException e){
        return RespBean.noLogin(e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(BaseErrorException.class)
    public RespBean<String> baseErrorHandle(BaseErrorException e){
        return RespBean.error(e.getMessage());
    }
}
