package com.CH.security.controller;

import com.CH.security.entity.TmUser;
import com.CH.security.model.dto.LoginFromDto;
import com.CH.security.service.ILoginService;
import com.CH.security.utils.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

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

    @PostMapping("/userLogin")
    public RespBean<TmUser> userLogin(@Valid LoginFromDto from,BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return RespBean.error(null, bindingResult.getFieldError().getDefaultMessage());
        }
        return loginService.login(from);
    }
}
