package com.springcloud.eurekaclient.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Title:
 * Description:
 *
 * @author 谭 tmn
 * @email AbelEthan@126.com
 * @date 2018/10/11 15:59
 */
@RestController
public class StatusController {

    @GetMapping("/status")
    public String status(){
        return "OK";
    }

}
