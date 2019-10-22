package com.springcloud.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.concurrent.TimeoutException;

/**
 * Title: DemoRestController
 * Description:
 *
 * @author 谭 tmn
 * @email AbelEthan@126.com
 * @date 2018/10/25 22:07
 */
@RestController
public class DemoRestController {

    private final static Random random = new Random();

    /**
     * 当方法执行时间超过 100 ms 时， 触发异常
     *
     * @return
     */
    @GetMapping("")
    public String index() throws TimeoutException {
        int executeTime = random.nextInt(200);
        //执行时间超过了 100 ms
        if (executeTime > 100){
            throw new TimeoutException("Execution is timeout");
        }
        return "Hello World";
    }
}

