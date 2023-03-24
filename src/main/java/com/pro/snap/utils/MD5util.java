package com.pro.snap.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

@Component
public class MD5util {
    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }

    public static final String salt = "1a2b3c4d";

    public static String inputPassToFormPass(String inputPass){
        String str = "" + salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(5) +salt.charAt(4);
        return md5(str);
    }

    public static String formPassToDBPass(String formPass, String salt){
        String str = "" + salt.charAt(0) + salt.charAt(2) + formPass + salt.charAt(5) +salt.charAt(4);
        return md5(str);
    }

    public static String inputPassToDBPass(String inputPass, String salt){
        String formPass = inputPassToFormPass(inputPass);
        return formPassToDBPass(formPass, salt);
    }

    public static void main(String[] args){
        String FormPass = inputPassToFormPass("123456");
        System.out.println("FormPass: " + FormPass);
        String DBPass = formPassToDBPass(FormPass, salt);
        System.out.println("DBPass: " + DBPass);
        String DBPass_check = inputPassToDBPass("123456", salt);
        System.out.println("DBPass_check: " + DBPass_check);
        System.out.println(DBPass_check.equals(DBPass));
    }
}
