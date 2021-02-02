package com.CH.security.exception;

/**
 * @author ch
 * @version 1.0
 * @date 2021/1/29 14:31
 */
public class BaseErrorException extends RuntimeException {
    public BaseErrorException(String msg) {
        super(msg);
    }
}
