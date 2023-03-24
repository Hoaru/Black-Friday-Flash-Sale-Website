package com.pro.snap.vo;

import com.pro.snap.utils.ValidatorUtil;
import com.pro.snap.validator.IsMobile;
import org.thymeleaf.util.StringUtils;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {

    private boolean required = false;

    @Override
    public void initialize(IsMobile constraintAnnotation){
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context){
        if(required){
            return ValidatorUtil.isMobile(value);
        }
        else{
            if(StringUtils.isEmpty(value)){
                return true;
            }
            else{
                return ValidatorUtil.isMobile(value);
            }
        }
    }
}
