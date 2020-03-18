package org.cloud.consumer.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "eureka-provider")
public interface UserConsumerService {
    /**
     * 查询所有的用户信息
     *
     * @param name
     * @return
     */
    @RequestMapping(value = "/users/list", method = RequestMethod.GET)
    String getUserList(@RequestParam(value = "name") String name);


    @RequestMapping(value = "/users/add", method = RequestMethod.GET)
    String addUser();
}
