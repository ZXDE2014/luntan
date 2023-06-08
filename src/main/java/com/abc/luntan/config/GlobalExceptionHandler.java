package com.abc.luntan.config;


import com.abc.luntan.utils.api.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


@Slf4j
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
    @ExceptionHandler({Throwable.class})
    public CommonResult<String> bingExceptionHandler(Throwable e) {
        //GET exception
        if (e instanceof BindException){
            List<ObjectError> allErrors = ((BindException) e).getBindingResult().getAllErrors();
            return CommonResult.validateFail(getValidErrorMsg(allErrors));
        }
        //POST exception
        if(e instanceof MethodArgumentNotValidException){
            List<ObjectError> allErrors = ((MethodArgumentNotValidException) e).getBindingResult().getAllErrors();
            return CommonResult.validateFail(getValidErrorMsg(allErrors));
        }

        return CommonResult.fail(e.getMessage());

    }


    @ExceptionHandler(value = RuntimeException.class)
    public CommonResult<String> exceptionHandler(Exception e) {
        log.error(e.getMessage(), e);
        return CommonResult.fail("服务器异常");
    }


    private String getValidErrorMsg(List<ObjectError> allErrors){
        if (!allErrors.isEmpty()){
            StringBuilder sb = new StringBuilder();
            allErrors.forEach(e->{
                if(e instanceof FieldError){
                    sb.append(((FieldError)e).getField()).append(":");
                }
                sb.append(e.getDefaultMessage()).append(";");
            });
            return sb.toString();
        }
        return null;
    }



}
