package com.CH.security.controller;

import com.CH.security.annotations.NoLogin;
import com.CH.security.model.dto.LoginFromDto;
import com.CH.security.model.dto.VscodeDto;
import com.CH.security.service.ILoginService;
import com.CH.security.utils.RespBean;
import com.CH.security.utils.TokenUtil;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author ch
 * @version 1.0
 * @date 2021/1/13 15:08
 */
@RestController
@RequestMapping("/login")
public class LoginController {
    @Resource
    ILoginService loginService;

    /**
     * 登录成功将token返回回去
     * @param from
     * @param bindingResult
     * @return
     */
    @NoLogin
    @PostMapping("/userLogin")
    public RespBean<String> userLogin(@Valid LoginFromDto from,BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return RespBean.error(null, bindingResult.getFieldError().getDefaultMessage());
        }
        return loginService.login(from);
    }


    @NoLogin
    @GetMapping("/refreshToken")
    public RespBean<String> refreshToken(HttpServletRequest request){
        return loginService.refreshToken(request);
    }

    @NoLogin
    @GetMapping("/getVscode")
    public RespBean<VscodeDto>  getVscode(Integer type, HttpServletResponse response) throws IOException, InstantiationException, IllegalAccessException {
        if(type==null){
            type=0;
        }
        return  loginService.getVscode(type,response);
    }


    /**
     * token测试
     * @return
     */
    @GetMapping("/test")
    public RespBean<String> test(HttpServletRequest request){
        String token = request.getHeader("token");
        long l = TokenUtil.analyzeToken(token);
        return RespBean.ok(String.valueOf(l));
    }
}
