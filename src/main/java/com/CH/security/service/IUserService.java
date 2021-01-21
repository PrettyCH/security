package com.CH.security.service;

import com.CH.security.entity.TmUser;
import com.CH.security.utils.RespBean;

/**
 * @author ch
 * @version 1.0
 * @date 2021/1/14 15:38
 */
public interface IUserService {
    RespBean<TmUser> getUserByUserId(String userId);
    RespBean<TmUser> getUserById(long id);
}
