package com.dreamone.validator;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

@Component
public class ValidatorImpl implements InitializingBean {

    private Validator validator;

    //实现校验方法并且返回错误
    public ValidationResult validate(Object bean) {
        final ValidationResult validationResult = new ValidationResult();
        Set<ConstraintViolation<Object>> validate = this.validator.validate(bean);
        if (validate.size() > 0) {
            //有错误
            validationResult.setHasErrors(true);
            validate.forEach(val->{
                String errMSg = val.getMessage();
                String propertyName= val.getPropertyPath().toString();
                validationResult.getErrorMsgMap().put(errMSg, propertyName);
            });
        }
        return validationResult;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //将hibernate validator 通过工厂实例化
        this.validator =  Validation.buildDefaultValidatorFactory().getValidator();
    }
}
