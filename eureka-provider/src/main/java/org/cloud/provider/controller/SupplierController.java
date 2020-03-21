package org.cloud.provider.controller;


import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.cloud.provider.entity.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/suppliers")
@Api(value = "eureka-provider", description = "供应商查询接口")
public class SupplierController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SupplierController.class);

    /**
     * 查询所有的供应商信息
     *
     * @param type
     * @return
     */
    @ResponseBody
    @GetMapping("/list")
    public String list(
            @ApiParam(value = "供应商") @RequestParam(required = false) int type) {
        String json;
        Map map = new HashMap<>();
        try {
            Supplier supplier = new Supplier(type);
            map.put("code", "0");
            map.put("msg", "查询成功");
            map.put("data", supplier);
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


}
