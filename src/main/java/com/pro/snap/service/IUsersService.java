package com.pro.snap.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pro.snap.pojo.Users;
import com.pro.snap.vo.LoginVo;
import com.pro.snap.vo.RespBean;
import com.pro.snap.vo.RespBeanEnum;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//服务类
public interface IUsersService extends IService<Users> {
    // 登录
    RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);

    // 根据cookie获取用户
    Users getUsersByCookie(String usersTicket, HttpServletRequest request, HttpServletResponse response);

    // 更新密码
    RespBean updatePassword(String userTicket, String password, HttpServletRequest request, HttpServletResponse response);
}