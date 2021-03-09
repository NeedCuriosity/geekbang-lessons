package org.geektimes.validator.bean.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UserValidAnnotationValidator.class)
public @interface UserValid {

    int passwordSizeMax() default 32;
    int passwordSizeMin() default 6;
    int phoneNumberSize() default 11;

    String message() default "{org.geektimes.validator.bean.validation.UserValid.message}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
