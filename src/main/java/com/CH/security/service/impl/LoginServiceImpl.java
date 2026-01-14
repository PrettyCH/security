package com.CH.security.service.impl;

import com.CH.security.entity.TmRole;
import com.CH.security.entity.TmUser;
import com.CH.security.entity.TmUserRole;
import com.CH.security.enums.EasyCaptchaEnum;
import com.CH.security.exception.BaseErrorException;
import com.CH.security.mapper.TmRoleMapper;
import com.CH.security.mapper.TmUserMapper;
import com.CH.security.mapper.TmUserRoleMapper;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
@Log4j2
public class LoginServiceImpl implements ILoginService {

    @Value("${config.jwt.secret}")
    private   String SECRET;

    // 默认过期时间是3600秒，既是1个小时
    @Value("${config.jwt.expire:3600}")
    private   long DEFAULT_EXPIRATION;

    // 管理员过期时间是172800秒，既是2天
    private final long ADMIN_EXPIRATION = 172800L;

    // 普通用户过期时间是3600秒，既是1个小时
    private final long USER_EXPIRATION = 3600L;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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

    @Resource
    TmUserRoleMapper userRoleMapper;

    @Resource
    TmRoleMapper roleMapper;

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
        queryWrapper.eq("user_id", from.getUserAccount());
        TmUser user = userMapper.selectOne(queryWrapper);
        if(user == null){
            return RespBean.error(null,"用户账户或密码有错");
        }
        // 使用BCrypt验证密码
        if(!passwordEncoder.matches(from.getUserPwd(), user.getUserPwd())){
            return RespBean.error(null,"用户账户或密码有错");
        }
        // 根据用户角色设置不同的Token过期时间
        long expiration = getExpirationByUserRole(user.getId());
        String token = TokenUtil.createToken(user.getId(), SECRET, expiration);
        return RespBean.ok(token);
    }

    /**
     * 根据用户角色获取Token过期时间
     * @param userId 用户ID
     * @return 过期时间（秒）
     */
    private long getExpirationByUserRole(Long userId) {
        // 查询用户角色
        QueryWrapper<TmUserRole> userRoleQuery = new QueryWrapper<>();
        userRoleQuery.eq("user_id", userId.intValue());
        List<TmUserRole> userRoles = userRoleMapper.selectList(userRoleQuery);
        
        // 检查用户是否有管理员角色
        for (TmUserRole userRole : userRoles) {
            QueryWrapper<TmRole> roleQuery = new QueryWrapper<>();
            roleQuery.eq("id", userRole.getRoleId());
            TmRole role = roleMapper.selectOne(roleQuery);
            if (role != null && "ADMIN".equals(role.getRoleId())) {
                return ADMIN_EXPIRATION;
            }
        }
        
        // 默认使用普通用户过期时间
        return USER_EXPIRATION;
    }

    @Override
    public RespBean<String> refreshToken(HttpServletRequest request) {
        String token = request.getHeader("token");
        String refreshToken;
        try{
            long userId = TokenUtil.analyzeToken(token);
            // 根据用户角色设置不同的Token过期时间
            long expiration = getExpirationByUserRole(userId);
            refreshToken = TokenUtil.createToken(userId, SECRET, expiration);
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
