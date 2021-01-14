package com.CH.security.service.impl;

import com.CH.security.entity.TmUser;
import com.CH.security.mapper.TmUserMapper;
import com.CH.security.service.IUserService;
import com.CH.security.utils.RespBean;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author ch
 * @version 1.0
 * @date 2021/1/14 15:38
 */
@Service
public class UserServiceImpl implements IUserService {
    @Resource
    TmUserMapper userMapper;
    @Override
    public RespBean<TmUser> getUserByUserId(String userId) {
        QueryWrapper<TmUser> query = new QueryWrapper<>();
        query.eq("user_id",userId);
        TmUser tmUser = userMapper.selectOne(query);
        if(tmUser==null){
            return RespBean.error(null,"该用户不存在");
        }
        return RespBean.ok(tmUser);
    }
}
