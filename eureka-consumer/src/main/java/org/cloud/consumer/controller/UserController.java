package org.cloud.consumer.controller;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.cloud.consumer.service.UserConsumerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/users")
@Api(value = "eureka-consumer", description = "用户查询接口")
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    final UserConsumerService userConsumerService;

    public UserController(UserConsumerService userConsumerService) {
        this.userConsumerService = userConsumerService;
    }

    /**
     * 查询所有的用户信息
     *
     * @param name
     * @return
     */
    @ResponseBody
    @GetMapping("/consumer/list")
    public String list(
            @ApiParam(value = "用户名") @RequestParam(required = false) String name) {
        Map modelMap = new HashMap<>();
        String json;
        try {
            json = userConsumerService.getUserList(name);
        } catch (Exception e) {
            e.printStackTrace();
            modelMap.put("ren_code", "0");
            modelMap.put("ren_msg", "查询失败===>" + e);
            LOGGER.error("查询失败===>" + e);
            json = JSON.toJSONString(modelMap);
        }
        return json;
    }


    /**
     * 查询所有的用户信息
     *
     * @return
     */
    @ResponseBody
    @GetMapping("/consumer/add")
    public String add() {
        Map modelMap = new HashMap<>();
        String json;
        try {
            json = userConsumerService.addUser();
        } catch (Exception e) {
            e.printStackTrace();
            modelMap.put("ren_code", "0");
            modelMap.put("ren_msg", "查询失败===>" + e);
            LOGGER.error("查询失败===>" + e);
            json = JSON.toJSONString(modelMap);
        }
        return json;
    }
}
