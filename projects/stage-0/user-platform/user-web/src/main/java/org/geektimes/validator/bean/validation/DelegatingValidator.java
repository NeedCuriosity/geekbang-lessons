package org.geektimes.validator.bean.validation;

import javax.annotation.PostConstruct;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.executable.ExecutableValidator;
import javax.validation.metadata.BeanDescriptor;
import java.util.Set;

/**
 * @author zhouzy
 * @since 2021-03-09
 */
public class DelegatingValidator implements Validator {

    private Validator validator;

    @PostConstruct
    public void init() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Override
    public <T> Set<ConstraintViolation<T>> validate(T t, Class<?>... classes) {
        return validator.validate(t, classes);
    }

    @Override
    public <T> Set<ConstraintViolation<T>> validateProperty(T t, String s, Class<?>... classes) {
        return validator.validateProperty(t, s, classes);
    }

    @Override
    public <T> Set<ConstraintViolation<T>> validateValue(Class<T> aClass, String s, Object o, Class<?>... classes) {
        return validator.validateValue(aClass, s, o, classes);
    }

    @Override
    public BeanDescriptor getConstraintsForClass(Class<?> aClass) {
        return validator.getConstraintsForClass(aClass);
    }

    @Override
    public <T> T unwrap(Class<T> aClass) {
        return validator.unwrap(aClass);
    }

    @Override
    public ExecutableValidator forExecutables() {
        return validator.forExecutables();
    }

}
