package com.CH.security.utils;

import java.util.List;

/**
 * 自己手写的一些工具类
 * @author ch
 * @version 1.0
 * @date 2021/1/13 14:55
 *
 */
public class MyUtil {
    /**
     * 判断list是否为空，空返回true，正常为false
     * @param list
     * @return
     */
    public static<T>  boolean  emptyList (List<T> list){
        if(list==null||list.size()==0){
            return  true;
        }
        return false;
    }
}
