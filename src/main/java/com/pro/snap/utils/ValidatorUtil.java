package com.pro.snap.utils;

import org.thymeleaf.util.StringUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatorUtil {
    private static final Pattern mobilePattern = Pattern.compile("[1][0-9]{10}$");

    public static boolean isMobile(String mobile){
        if(StringUtils.isEmpty(mobile))
            return false;
        Matcher matcher = mobilePattern.matcher(mobile);
        return matcher.matches();
    }
}
