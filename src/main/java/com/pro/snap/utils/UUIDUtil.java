package com.pro.snap.utils;

import java.util.UUID;

//UUID工具类
public class UUIDUtil {
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}