package com.springcloud.consulclient.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Title: 检查 Controller
 * Description:
 *
 * @author 谭 tmn
 * @email AbelEthan@126.com
 * @date 2018/10/18 11:06
 */
@RestController
public class CheckController {

    @GetMapping("/check")
    public String check(){
        return "OK";
    }
}
