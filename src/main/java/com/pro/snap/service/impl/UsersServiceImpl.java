package com.pro.snap.service.impl;

import com.pro.snap.exception.GlobalException;
import com.pro.snap.mapper.UsersMapper;
import com.pro.snap.pojo.Users;
import com.pro.snap.service.IUsersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pro.snap.utils.CookieUtil;
import com.pro.snap.utils.MD5util;
import com.pro.snap.utils.UUIDUtil;
import com.pro.snap.vo.LoginVo;
import com.pro.snap.vo.RespBean;
import com.pro.snap.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//服务实现类
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements IUsersService {

    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    // 登录
    @Override
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
//        // 参数校验
//        if(StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password)){
//            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
//        }
//        if(!ValidatorUtil.isMobile(mobile)){
//            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
//        }
        // 用户是否存在
        Users users = usersMapper.selectById(mobile);
//        if(users == null){
//            return RespBean.error(RespBeanEnum.NOTFOUND_ERROR);
//        }
        if(users == null){
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        // 密码正确与否
//        if(!MD5util.formPassToDBPass(password, users.getSalt()).equals(users.getPassword())){
//            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
//        }
        if(!MD5util.formPassToDBPass(password, users.getSalt()).equals(users.getPassword())){
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        // 生成cookie
        String ticket = UUIDUtil.uuid();
        //request.getSession().setAttribute(ticket, users);
        // 将用户信息存入redis中
        redisTemplate.opsForValue().set("users:" + ticket, users);
        CookieUtil.setCookie(request, response, "usersTicket", ticket);
        return RespBean.success(ticket);
    }

    // 通过Cookie获取用户
    @Override
    public Users getUsersByCookie(String usersTicket, HttpServletRequest request, HttpServletResponse response) {
        if(StringUtils.isEmpty(usersTicket)){
            return null;
        }
        Users users = (Users) redisTemplate.opsForValue().get("users:" + usersTicket);
        if(users != null){
            CookieUtil.setCookie(request, response, "usersTicket", usersTicket);
        }
        return users;
    }

    //更新密码
    @Override
    public RespBean updatePassword(String usersTicket, String password, HttpServletRequest request, HttpServletResponse response) {
        Users users = getUsersByCookie(usersTicket, request, response);
        if(users == null){
            throw new GlobalException(RespBeanEnum.MOBILE_NOT_EXIST);
        }
        users.setPassword(MD5util.inputPassToDBPass(password, users.getSalt()));
        int result = usersMapper.updateById(users);
        if(result == 1){
            // 删除Redis
            redisTemplate.delete("users: " + usersTicket);
            return RespBean.success();
        }
        return RespBean.error(RespBeanEnum.PASSWORD_UPDATE_FAILED);
    }
}
