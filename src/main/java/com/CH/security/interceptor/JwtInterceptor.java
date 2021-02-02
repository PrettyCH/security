package com.CH.security.interceptor;

import com.CH.security.annotations.NoLogin;
import com.CH.security.entity.TmUser;
import com.CH.security.exception.NoLoginException;
import com.CH.security.service.IUserService;
import com.CH.security.utils.RespBean;
import com.CH.security.utils.TokenUtil;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author ch
 * @version 1.0
 * @date 2021/1/14 15:26
 * 对拦截下来的请求进行token校验
 */
public class JwtInterceptor implements HandlerInterceptor {
    @Value("${config.jwt.secret:'testToken'}")
    private   String SECRET;

    @Resource
    private IUserService userService;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) throws Exception {
        // 如果不是映射到方法直接通过
        if (!(object instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) object;
        Method method = handlerMethod.getMethod();
        //检查是否有passtoken注释，有则跳过认证
        if (method.isAnnotationPresent(NoLogin.class)) {
            NoLogin noLogin = method.getAnnotation(NoLogin.class);
            if (noLogin.required()) {
                return true;
            }
        }
        String token = httpServletRequest.getHeader("token"); // 从 http 请求头中取出 token
        //检查有没有需要用户权限的注解
        // 执行认证
        if (token == null||token=="") {
            throw new NoLoginException("无token，请重新登录");
        }
        // 获取 token 中的 user id
        Long id;
        try {
            id = TokenUtil.analyzeToken(token);
        } catch (JWTDecodeException j) {
            throw new NoLoginException("token中无用户信息，请重新登录");
        }
        RespBean<TmUser> r = userService.getUserById(id);
        if (r.isError()) {
            throw new NoLoginException("用户不存在，请重新登录");
        }
        // 验证 token

        try {
            boolean b = TokenUtil.verifyToken(token, SECRET);
            if(b){
                return true;
            }else {
              throw new NoLoginException("token令牌无效，请重新登录");
            }
        } catch (JWTVerificationException e) {
            throw new NoLoginException("token中无用户信息，请重新登录");
        }
    }


    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest,
                                HttpServletResponse httpServletResponse,
                                Object o, Exception e) throws Exception {
    }

}
