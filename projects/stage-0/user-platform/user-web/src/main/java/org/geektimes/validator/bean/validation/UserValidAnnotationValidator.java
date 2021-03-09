package org.geektimes.validator.bean.validation;

import org.geektimes.projects.user.domain.User;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserValidAnnotationValidator implements ConstraintValidator<UserValid, User> {

    private int passwordSizeMin;
    private int passwordSizeMax;
    private int phoneNumberSize;

    public void initialize(UserValid annotation) {
        this.passwordSizeMax = annotation.passwordSizeMax();
        this.passwordSizeMin = annotation.passwordSizeMin();
        this.phoneNumberSize = annotation.phoneNumberSize();
    }

    @Override
    public boolean isValid(User value, ConstraintValidatorContext context) {

        // 获取模板信息
        context.getDefaultConstraintMessageTemplate();
        String password = value.getPassword();
        String phoneNumber = value.getPhoneNumber();
        if (password != null
                && phoneNumber != null
                && password.length() <= passwordSizeMax
                && password.length() >= passwordSizeMin
                && phoneNumber.length() == phoneNumberSize) {
            return true;
        } else {
            context.disableDefaultConstraintViolation();
            if (password == null || phoneNumber == null) {
                context.buildConstraintViolationWithTemplate("password或phoneNumber有空值")
                        .addConstraintViolation();
            } else if (password.length() > passwordSizeMax
                    || password.length() < passwordSizeMin) {
                context.buildConstraintViolationWithTemplate(String.format("password长度应在%d和%d之间", passwordSizeMin, passwordSizeMax))
                        .addConstraintViolation();
            } else {
                context.buildConstraintViolationWithTemplate(String.format("phoneNumber长度必须为%d", phoneNumberSize))
                        .addConstraintViolation();
            }
            return false;
        }
    }
}
