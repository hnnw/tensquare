package com.tensquare.web.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.tensquare.web.properties.FilterProperties;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import util.JwtUtil;


import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Component
@EnableConfigurationProperties(FilterProperties.class)
public class WebFilter extends ZuulFilter{

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private FilterProperties list;



    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        //获取header
        HttpServletRequest request = requestContext.getRequest();
        if(request.getMethod().equals("OPTIONS")){
            return false;
        }
        String uri = request.getRequestURI();
        System.out.println(uri);
        return !isAllowPath(uri);
    }
    private boolean isAllowPath(String requestURI) {

        // 遍历允许访问的路径
        for (String path : this.list.getAllowPaths()) {
            // 然后判断是否是符合
            if(requestURI.startsWith(path)){
                return true;
            }
        }
        return false;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        //获取header
        HttpServletRequest request = requestContext.getRequest();
        String authHeader =(String)request.getHeader("Authorization");//获取头信息
        if(authHeader!=null && authHeader.startsWith("Bearer ") ){
            String token = authHeader.substring(7);
            try {
            Claims claims = jwtUtil.parseJWT(token);
            if (claims!=null){
                requestContext.addZuulRequestHeader("Authorization",authHeader);
                String s =(String) claims.get("roles");
                return null;
            } }catch (Exception e){
                requestContext.setSendZuulResponse(false);//终止运行
                requestContext.setResponseStatusCode(401);//http状态码
            }
        }

        requestContext.setSendZuulResponse(false);//终止运行
        requestContext.setResponseStatusCode(401);//http状态码
        return null;
    }



}
