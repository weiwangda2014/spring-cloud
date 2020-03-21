package org.cloud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ConsumerController {

    // RestTemplate 是有SpringBoot Web组件提供 默认整合ribbon负载均衡器
    // rest方式底层是采用httpclient技术
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 在SpringCloud 中有两种方式调用 rest、fegin（SpringCloud）
     *
     * @return
     */

    @RequestMapping("/getFromProvider")
    public String getFromProvider() {
        // 有两种方式，一种是采用服务别名方式调用，另一种是直接调用 使用别名去注册中心上获取对应的服务调用地址
        String url = "http://PROVIDER1/getServerPort";
        String result = restTemplate.getForObject(url, String.class);
        System.out.println("restTemplate 调用 服务提供者服务 :" + result);
        return result;
    }

}
