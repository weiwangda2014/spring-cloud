package org.cloud.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class Provider2Controller {

    @GetMapping("/getMsgFromProvider2")
    @ResponseBody
    public String getMsgFromProvider2() {
        return "我是第二个服务提供者";
    }

}
