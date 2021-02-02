package com.CH.security.service;

import com.CH.security.entity.TmUser;
import com.CH.security.model.dto.LoginFromDto;
import com.CH.security.model.dto.VscodeDto;
import com.CH.security.utils.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface ILoginService {
    RespBean<String> login(LoginFromDto from);

    RespBean<String> refreshToken(HttpServletRequest request);

    RespBean<VscodeDto> getVscode(Integer type, HttpServletResponse response) throws IOException, IllegalAccessException, InstantiationException;

}
