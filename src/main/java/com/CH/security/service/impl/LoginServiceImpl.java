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

import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
@Log4j2
public class LoginServiceImpl implements ILoginService {

    @Value("${config.jwt.secret}")
    private String jwtSecret;

    // 默认过期时间是3600秒，既是1个小时
    @Value("${config.jwt.expire:3600}")
    private long defaultExpiration;

    // 管理员过期时间是172800秒，既是2天
    @Value("${config.jwt.admin.expire:172800}")
    private long adminExpiration;

    // 普通用户过期时间是3600秒，既是1个小时
    @Value("${config.jwt.user.expire:3600}")
    private long userExpiration;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // 验证码缓存
    private static final Cache<String, String> VALIDATION_CODE_CACHE = CacheBuilder.newBuilder()
            // 设置cache的初始大小为10
            .initialCapacity(10)
            // 设置并发数为5，即同一时间最多只能有5个线程往cache执行写入操作
            .concurrencyLevel(5)
            // 设置cache中的数据在写入之后的存活时间为5分钟
            .expireAfterWrite(5, TimeUnit.MINUTES)
            // 构建cache实例
            .build();

    @Resource
    TmUserMapper userMapper;

    @Resource
    TmUserRoleMapper userRoleMapper;

    @Resource
    TmRoleMapper roleMapper;

    @Override
    public RespBean<String> login(LoginFromDto from) {
        log.info("用户登录尝试，账户: {}", from.getUserAccount());
        
        String vsCode = from.getVsCode();
        String uuid = from.getUuid();
        String cachedCode = VALIDATION_CODE_CACHE.getIfPresent(uuid);
        
        if(cachedCode == null){
            log.warn("登录失败2，验证码已过期，账户: {}", from.getUserAccount());
            return RespBean.error(null,"该验证码已过期");
        }
        
        if(!cachedCode.equals(vsCode)){
            log.warn("登录失败，验证码错误，账户: {}", from.getUserAccount());
            return RespBean.error(null,"验证码错误");
        }
        
        QueryWrapper<TmUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", from.getUserAccount());
        TmUser user = userMapper.selectOne(queryWrapper);
        
        if(user == null){
            log.warn("登录失败，用户不存在，账户: {}", from.getUserAccount());
            return RespBean.error(null,"用户账户或密码有错");
        }
        
        // 使用BCrypt验证密码
        if(!passwordEncoder.matches(from.getUserPwd(), user.getUserPwd())){
            log.warn("登录失败，密码错误，账户: {}", from.getUserAccount());
            return RespBean.error(null,"用户账户或密码有错");
        }
        
        // 根据用户角色设置不同的Token过期时间
        long expiration = getExpirationByUserRole(user.getId());
        String token = TokenUtil.createToken(user.getId(), jwtSecret, expiration);
        
        log.info("用户登录成功，账户: {}, 用户ID: {}", from.getUserAccount(), user.getId());
        return RespBean.ok(token);
    }

    /**
     * 根据用户角色获取Token过期时间
     * @param userId 用户ID
     * @return 过期时间（秒）
     */
    private long getExpirationByUserRole(Long userId) {
        // 查询用户角色关联
        QueryWrapper<TmUserRole> userRoleQuery = new QueryWrapper<>();
        userRoleQuery.eq("user_id", userId.intValue());
        List<TmUserRole> userRoles = userRoleMapper.selectList(userRoleQuery);
        
        // 如果用户没有角色，使用普通用户过期时间
        if (userRoles == null || userRoles.isEmpty()) {
            return userExpiration;
        }
        
        // 提取所有角色ID
        List<Integer> roleIds = userRoles.stream()
                .map(TmUserRole::getRoleId)
                .collect(java.util.stream.Collectors.toList());
        
        // 一次性查询所有角色信息
        QueryWrapper<TmRole> roleQuery = new QueryWrapper<>();
        roleQuery.in("id", roleIds);
        List<TmRole> roles = roleMapper.selectList(roleQuery);
        
        // 检查是否有管理员角色
        for (TmRole role : roles) {
            if (role != null && "ADMIN".equals(role.getRoleId())) {
                return adminExpiration;
            }
        }
        
        // 默认使用普通用户过期时间
        return userExpiration;
    }

    @Override
    public RespBean<String> refreshToken(HttpServletRequest request) {
        String token = request.getHeader("token");
        
        // 检查token是否为空
        if (token == null || token.isEmpty()) {
            log.warn("Token刷新失败，token为空");
            throw new BaseErrorException("token为空，无法续期");
        }
        
        log.info("Token刷新尝试，token: {}", token.substring(0, 20) + "...");
        
        String refreshToken;
        try{
            long userId = TokenUtil.analyzeToken(token);
            log.info("Token刷新，用户ID: {}", userId);
            
            // 根据用户角色设置不同的Token过期时间
            long expiration = getExpirationByUserRole(userId);
            refreshToken = TokenUtil.createToken(userId, jwtSecret, expiration);
            
            log.info("Token刷新成功，用户ID: {}", userId);
        }catch (Exception e){
            log.error("Token刷新失败: {}", e.getMessage(), e);
            throw new BaseErrorException("token续期失败");
        }
        
        return RespBean.ok(refreshToken);
    }

    @Override
    public RespBean<VscodeDto> getVscode(Integer type, HttpServletResponse response) throws IOException, IllegalAccessException, InstantiationException {
        // 设置默认验证码类型为0
        if (type == null) {
            type = 0;
            log.info("验证码类型为空，使用默认类型: {}", type);
        } else {
            log.info("生成验证码，类型: {}", type);
        }
        
        Class<?> className = EasyCaptchaEnum.getNameByCode(type);
        Captcha captcha = (Captcha)className.newInstance();
        String text = captcha.text();// 获取验证码的字符
        UUID uuid = UUID.randomUUID();
        VscodeDto vscodeDto = new VscodeDto(uuid.toString(), captcha.toBase64());
        VALIDATION_CODE_CACHE.put(uuid.toString(), text);
        
        log.debug("验证码生成成功，UUID: {}, 验证码: {}", uuid, text);
        return RespBean.ok(vscodeDto);
    }

}
