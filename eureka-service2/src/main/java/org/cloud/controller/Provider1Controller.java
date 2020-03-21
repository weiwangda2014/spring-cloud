package org.cloud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

@Controller
public class Provider1Controller {

    @Value("${server.port}")
    private String serverPort;

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/getServerPort")
    @ResponseBody
    public String getMember() {

        String url = "http://PROVIDER2/getMsgFromProvider2";
        String result = restTemplate.getForObject(url, String.class);
        System.out.println("restTemplate 调用 第二个服务提供者的服务 服务提供者服务 :" + result);
        return result;

    }

}