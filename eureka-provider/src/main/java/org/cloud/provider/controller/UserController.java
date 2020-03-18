package org.cloud.provider.controller;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.cloud.provider.entity.User;
import org.cloud.provider.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/users")
@Api(value = "eureka-provider", description = "用户查询接口")
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    protected final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 查询所有的用户信息
     *
     * @param name
     * @return
     */
    @ResponseBody
    @GetMapping("/list")
    public String list(
            @ApiParam(value = "用户名") @RequestParam(required = false) String name) {
        String json;
        Map map = new HashMap<>();
        try {
            User user = new User();
            user.setLoginName(name);
            Example<User> example = Example.of(user);
            List<User> users = userRepository.findAll(example);
            map.put("code", "0");
            map.put("msg", "查询成功");
            map.put("users", users);
            json = JSON.toJSONString(map);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("code", "0");
            map.put("msg", "查询失败===>" + e);
            LOGGER.error("查询失败===>" + e);
            json = JSON.toJSONString(map);
        }
        return json;
    }


    @ResponseBody
    @GetMapping("/add")
    public String add() {
        String json;
        Map map = new HashMap<>();
        try {
            User user = new User();
            user.setId(UUID.randomUUID().toString());
            String s = new Random().nextDouble() + "";
            user.setLoginName(s + "my");
            user.setUserName(s + "my");
            user.setEnabled(0);
            user.setRegistDate(new Date());
            user.setPassword(s);
            User users = userRepository.save(user);
            map.put("code", "0");
            map.put("msg", "保存成功");
            map.put("users", users);
            json = JSON.toJSONString(map);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("code", "0");
            map.put("msg", "保存失败===>" + e);
            LOGGER.error("保存失败===>" + e);
            json = JSON.toJSONString(map);
        }
        return json;
    }
}
