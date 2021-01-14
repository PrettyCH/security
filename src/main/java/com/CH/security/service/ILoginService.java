package com.CH.security.service;

import com.CH.security.entity.TmUser;
import com.CH.security.model.dto.LoginFromDto;
import com.CH.security.utils.RespBean;

public interface ILoginService {
    RespBean<TmUser> login(LoginFromDto from);
}
