package com.CH.security.service.impl;

import com.CH.security.entity.TmUser;
import com.CH.security.enums.EasyCaptchaEnum;
import com.CH.security.exception.BaseErrorException;
import com.CH.security.mapper.TmUserMapper;
import com.CH.security.model.dto.LoginFromDto;
import com.CH.security.model.dto.VscodeDto;
import com.CH.security.service.ILoginService;
import com.CH.security.utils.MyUtil;
import com.CH.security.utils.RespBean;
import com.CH.security.utils.TokenUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.wf.captcha.base.Captcha;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
@Log4j2
public class LoginServiceImpl implements ILoginService {

    @Value("${config.jwt.secret:'testToken'}")
    private   String SECRET;

    // 过期时间是3600秒，既是1个小时
    @Value("${config.jwt.expire:'3600'}")
    private   long EXPIRATION;

    final static Cache<String, String> VSCODE_CACHE = CacheBuilder.newBuilder()
            //设置cache的初始大小为10，要合理设置该值
            .initialCapacity(10)
            //设置并发数为5，即同一时间最多只能有5个线程往cache执行写入操作
            .concurrencyLevel(5)
            //设置cache中的数据在写入之后的存活时间为10秒
            .expireAfterWrite(5, TimeUnit.MINUTES)
            //构建cache实例
            .build();

    @Resource
    TmUserMapper userMapper;

    @Override
    public RespBean<String> login(LoginFromDto from) {
        String vsCode = from.getVsCode();
        String uuid = from.getUuid();
        String ifPresent = VSCODE_CACHE.getIfPresent(uuid);
        if(ifPresent==null){
            return RespBean.error(null,"该验证码已过期");
        }
        if(!ifPresent.equals(vsCode)){
            return RespBean.error(null,"验证码错误");
        }
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
        String token = TokenUtil.createToken(userList.get(0).getId(),SECRET,EXPIRATION);
        return RespBean.ok(token);
    }

    @Override
    public RespBean<String> refreshToken(HttpServletRequest request) {
        String token = request.getHeader("token");
        String refreshToken;
        try{
            long l = TokenUtil.analyzeToken(token);
           refreshToken = TokenUtil.createToken(l, SECRET, EXPIRATION);
        }catch (Exception e){
            log.error("token续期失败",e.getMessage(),e);
            throw new BaseErrorException("token续期失败");
        }
        return RespBean.ok(refreshToken);
    }

    @Override
    public RespBean<VscodeDto> getVscode(Integer type, HttpServletResponse response) throws IOException, IllegalAccessException, InstantiationException {
        Class className = EasyCaptchaEnum.getNameByCode(type);
        Captcha captcha = (Captcha)className.newInstance();
        String text = captcha.text();// 获取验证码的字符
        UUID uuid = UUID.randomUUID();
        VscodeDto vscodeDto = new VscodeDto(uuid.toString(),captcha.toBase64());
        VSCODE_CACHE.put(uuid.toString(),text);
        return RespBean.ok(vscodeDto);
    }

}
