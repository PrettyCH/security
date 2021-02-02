package com.CH.security.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

/**
 * @author ch
 * @version 1.0
 * @date 2021/1/14 15:11
 */
public class TokenUtil {

    private final static String ISS = "CH";

    /**
     * 生成token
     * @param userId 用户id
     * @return token
     */
    public static String createToken(long userId,String secret,Long expiration){
        Algorithm algorithm = Algorithm.HMAC384(secret);
        String token = JWT.create()
                .withIssuer(ISS)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expiration*1000))
                .withClaim("USER_ID",userId)
                .sign(algorithm);

        return token;
    }

    /**
     * 解析token获取用户ID
     * @param token token
     * @return long
     */
    public static long analyzeToken(String token){
        DecodedJWT decode = JWT.decode(token);
        return decode.getClaim("USER_ID").asLong();
    }

    /**
     * 校验token的合法性
     * @param token token
     * @return  boolean
     */
    public static boolean verifyToken(String token,String secret){
        Algorithm algorithm = Algorithm.HMAC384(secret);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(ISS)
                .build(); //Reusable verifier instance
        try {
            // 验证不通过会出现异常
            verifier.verify(token);
        } catch(Exception ex){
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        System.out.println(new Date(System.currentTimeMillis()+60*1000));
    }
}
