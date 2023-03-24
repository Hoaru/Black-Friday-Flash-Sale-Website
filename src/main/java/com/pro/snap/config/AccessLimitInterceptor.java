package com.pro.snap.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pro.snap.pojo.Users;
import com.pro.snap.service.IUsersService;
import com.pro.snap.utils.CookieUtil;
import com.pro.snap.vo.RespBean;
import com.pro.snap.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

// 接口限流拦截器
@Component
public class AccessLimitInterceptor implements HandlerInterceptor {

    @Autowired
    private IUsersService usersService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod) {
            Users users = getUsers(request, response);
            UsersContext.setUsers(users);
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            AccessLimit accessLimit = handlerMethod.getMethodAnnotation(AccessLimit.class);
            if(accessLimit == null){
                return true;
            }
            int second = accessLimit.second();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            String key = request.getRequestURI();
            if(needLogin) {
                if(users == null) {
                    render(response, RespBeanEnum.USER_NOT_EXIST);
                    return false;
                }
                key = key + ":" + users.getId();
            }
            ValueOperations valueOperations = redisTemplate.opsForValue();
            Integer count = (Integer) valueOperations.get(key);
            if(count == null) {
                valueOperations.set(key, 1, second, TimeUnit.SECONDS);
            }
            else if(count < maxCount) {
                valueOperations.increment(key);
            }
            else {
                render(response, RespBeanEnum.FREQUENT_ACCESS);
                return false;
            }
        }
        return true;
    }

    // 构建返回对象
    private void render(HttpServletResponse response, RespBeanEnum respBeanEnum) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        RespBean respBean = RespBean.error(respBeanEnum);
        out.write(new ObjectMapper().writeValueAsString(respBean));
        out.flush();
        out.close();
    }

    // 获取当前用户
    private Users getUsers(HttpServletRequest request, HttpServletResponse response) {
        String ticket = CookieUtil.getCookieValue(request, "usersTicket");
        if(StringUtils.isEmpty(ticket)){
            return null;
        }
        return usersService.getUsersByCookie(ticket, request, response);
    }
}
