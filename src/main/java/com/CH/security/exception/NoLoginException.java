package com.CH.security.exception;

/**
 * @author ch
 * @version 1.0
 * @date 2021/1/28 16:07
 */
public class NoLoginException extends  RuntimeException{
    public NoLoginException(String msg) {
        super(msg);
    }
}
