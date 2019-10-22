package com.springcloud.web.controller;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.concurrent.TimeoutException;

/**
 * Title: DemoRestControllerAdvice
 * Description: {@link DemoRestController} 类似于 AOP 拦截
 *
 * @author 谭 tmn
 * @email AbelEthan@126.com
 * @date 2018/10/25 22:07
 */
@RestControllerAdvice(assignableTypes = DemoRestController.class)
public class DemoRestControllerAdvice {

    @ExceptionHandler(TimeoutException.class)
    public Object faultToleranceTimeout(Throwable throwable){
        return throwable.getMessage();
    }

}

