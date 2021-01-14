package com.CH.security.service.impl;

import com.CH.security.entity.TmUser;
import com.CH.security.mapper.TmUserMapper;
import com.CH.security.model.dto.LoginFromDto;
import com.CH.security.service.ILoginService;
import com.CH.security.utils.MyUtil;
import com.CH.security.utils.RespBean;
import com.CH.security.utils.TokenUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class LoginServiceImpl implements ILoginService {

    @Resource
    TmUserMapper userMapper;

    @Override
    public RespBean<String> login(LoginFromDto from) {
        QueryWrapper<TmUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",from.getUserAccount()).eq("user_pwd", from.getUserPwd());
        List<TmUser> userList = userMapper.selectList(queryWrapper);
        boolean b = MyUtil.emptyList(userList);
        if(b){
            return RespBean.error(null,"用户账户或密码有错");
        }
        if(userList.size()!=1){
            return RespBean.error(null,"用户出错，用户账户不唯一");
        }
        String token = TokenUtil.getToken(userList.get(0));
        return RespBean.ok(token);
    }
}
