package com.pro.snap.config;

import com.pro.snap.pojo.Users;

public class UsersContext {

    // ThreadLocal解决每个线程绑定自己的值。高并发多线程场景下，若在公共资源中存储用户信息，可能会导致用户信息紊乱，所以可以把当前用户信息存在自身所在线程中
    private static ThreadLocal<Users> usersHolder = new ThreadLocal<>();

    public static void setUsers(Users users) {
        usersHolder.set(users);
    }

    public static Users getUsers(){
        return usersHolder.get();
    }
}
