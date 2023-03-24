package com.pro.snap.exception;

import com.pro.snap.vo.RespBean;
import com.pro.snap.vo.RespBeanEnum;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public RespBean ExceptionHandler(Exception e){
        if(e instanceof GlobalException){
            GlobalException ex = (GlobalException) e;
            return RespBean.error(ex.getRespbeanEnum());
        }
        else if(e instanceof BindException){
            BindException ex = (BindException)e;
            RespBean respBean =  RespBean.error(RespBeanEnum.BIND_ERROR);
            respBean.setMessage("ARGUMENT INVALID EXCEPTION: " + ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
            return respBean;
        }
        else{
            return RespBean.error(RespBeanEnum.ERROR);
        }
    }
}
