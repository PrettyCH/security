package com.CH.security.utils;

import com.CH.security.entity.TmUser;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

/**
 * @author ch
 * @version 1.0
 * @date 2021/1/14 15:11
 */
public class TokenUtil {
    public static String getToken(TmUser user) {
        String token="";
        token= JWT.create().withAudience(user.getUserId())
                .sign(Algorithm.HMAC256(user.getUserPwd()));
        return token;
    }
}
