package com.CH.security.service.impl;

import com.CH.security.entity.TmUser;
import com.CH.security.mapper.TmUserMapper;
import com.CH.security.service.IUserService;
import com.CH.security.utils.RespBean;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

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
        if(StringUtils.isBlank(userId)){
            return RespBean.error(null,"用户账户名不能为空");
        }
        QueryWrapper<TmUser> query = new QueryWrapper<>();
        query.eq("user_id",userId);
        TmUser tmUser = userMapper.selectOne(query);
        if(tmUser==null){
            return RespBean.error(null,"该用户不存在");
        }
        return RespBean.ok(tmUser);
    }

    @Override
    public RespBean<TmUser> getUserById(long id) {
        TmUser tmUser = userMapper.selectById(id);
        if(tmUser==null){
            return RespBean.error(null,"该用户不存在");
        }
        return RespBean.ok(tmUser);
    }
}
