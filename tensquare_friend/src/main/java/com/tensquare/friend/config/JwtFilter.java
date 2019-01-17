package com.tensquare.friend.config;

import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import util.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends HandlerInterceptorAdapter {
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler)
            throws Exception {
        String authorization = request.getHeader("Authorization");

        if (!StringUtils.isEmpty(authorization)){
            String substring = StringUtils.substring(authorization, 7);
            try {
                Claims claims = jwtUtil.parseJWT(substring);
               String roles = (String) claims.get("roles");

               if (!StringUtils.isEmpty(roles)){
                   if ("admin".equals(roles)){
                       request.setAttribute("admin_claims",claims);
                   }
                   if ("user".equals(roles)){
                       request.setAttribute("user_claims",claims);
                   }
               }
            }catch (Exception e){
                throw new RuntimeException("权限过期");
            }


        }
        return true;
    }
}