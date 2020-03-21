package org.cloud.provider.controller;


import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.cloud.provider.entity.Trips;
import org.cloud.provider.entity.User;
import org.cloud.provider.repository.TripsRepository;
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
@RequestMapping("/trips")
@Api(value = "eureka-provider", description = "行程查询接口")
public class TripsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TripsController.class);

    protected final TripsRepository tripsRepository;

    public TripsController(TripsRepository tripsRepository) {
        this.tripsRepository = tripsRepository;
    }

    /**
     * 查询所有的行程信息
     *
     * @param start
     * @param end
     * @return
     */
    @ResponseBody
    @GetMapping("/list")
    public String list(
            @ApiParam(value = "出发") @RequestParam(required = false) String start, @ApiParam(value = "终点") @RequestParam(required = false) String end) {
        String json;
        Map map = new HashMap<>();
        try {
            Trips trips = new Trips();
            trips.setStart(start);
            trips.setEnd(end);
            Example<Trips> example = Example.of(trips);
            List<Trips> list = tripsRepository.findAll(example);
            map.put("code", "0");
            map.put("msg", "查询成功");
            map.put("data", list);
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
    public String add( @ApiParam(value = "出发") @RequestParam(required = false) String start, @ApiParam(value = "终点") @RequestParam(required = false) String end) {
        String json;
        Map map = new HashMap<>();
        try {
            Trips trips = new Trips();
            trips.setId(UUID.randomUUID().toString());
            trips.setStart(start);
            trips.setEnd(end);
            trips = tripsRepository.save(trips);
            map.put("code", "0");
            map.put("msg", "保存成功");
            map.put("data", trips);
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
