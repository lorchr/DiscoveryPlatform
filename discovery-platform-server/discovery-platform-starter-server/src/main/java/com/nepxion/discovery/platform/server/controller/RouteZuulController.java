package com.nepxion.discovery.platform.server.controller;

/**
 * <p>Title: Nepxion Discovery</p>
 * <p>Description: Nepxion Discovery</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Ning Zhang
 * @version 1.0
 */

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.core.type.TypeReference;
import com.nepxion.discovery.common.entity.ResultEntity;
import com.nepxion.discovery.common.util.JsonUtil;
import com.nepxion.discovery.console.resource.RouteResource;
import com.nepxion.discovery.console.resource.ServiceResource;
import com.nepxion.discovery.platform.server.entity.dto.RouteZuulDto;
import com.nepxion.discovery.platform.server.entity.po.RouteZuulPo;
import com.nepxion.discovery.platform.server.entity.response.Result;
import com.nepxion.discovery.platform.server.entity.vo.RouteZuulVo;
import com.nepxion.discovery.platform.server.service.RouteZuulService;
import com.nepxion.discovery.platform.server.tool.CommonTool;

@Controller
@RequestMapping(RouteZuulController.PREFIX)
public class RouteZuulController {
    public static final String PREFIX = "routezuul";

    @Autowired
    private ServiceResource serviceResource;

    @Autowired
    private RouteResource routeResource;

    @Autowired
    private RouteZuulService routeZuulService;

    @GetMapping("list")
    public String list() {
        return String.format("%s/%s", PREFIX, "list");
    }

    @GetMapping("working")
    public String working(Model model) {
        model.addAttribute("gatewayNames", serviceResource.getGatewayList(RouteZuulService.GATEWAY_TYPE));
        return String.format("%s/%s", PREFIX, "working");
    }

    @GetMapping("add")
    public String add(Model model) {
        model.addAttribute("gatewayNames", serviceResource.getGatewayList(RouteZuulService.GATEWAY_TYPE));
        model.addAttribute("serviceNames", serviceResource.getServices());
        return String.format("%s/%s", PREFIX, "add");
    }

    @GetMapping("edit")
    public String edit(Model model, @RequestParam(name = "id") Long id) {
        model.addAttribute("gatewayNames", serviceResource.getGatewayList(RouteZuulService.GATEWAY_TYPE));
        model.addAttribute("serviceNames", serviceResource.getServices());
        model.addAttribute("route", routeZuulService.getById(id));
        return String.format("%s/%s", PREFIX, "edit");
    }

    @PostMapping("do-list")
    @ResponseBody
    public Result<List<RouteZuulDto>> doList(@RequestParam(value = "page") Integer pageNum, @RequestParam(value = "limit") Integer pageSize, @RequestParam(value = "description", required = false) String description) {
        IPage<RouteZuulDto> page = routeZuulService.page(description, pageNum, pageSize);
        return Result.ok(page.getRecords(), page.getTotal());
    }

    @PostMapping("do-list-working")
    @ResponseBody
    public Result<List<RouteZuulVo>> doListWorking(@RequestParam(value = "gatewayName", required = false) String gatewayName) {
        if (StringUtils.isEmpty(gatewayName)) {
            return Result.ok();
        }

        List<RouteZuulVo> result = new ArrayList<>();
        List<ResultEntity> resultEntityList = routeResource.viewAllRoute(RouteZuulService.GATEWAY_TYPE, gatewayName);
        for (ResultEntity resultEntity : resultEntityList) {
            RouteZuulVo routeZuulVo = new RouteZuulVo();
            routeZuulVo.setHost(resultEntity.getHost());
            routeZuulVo.setPort(String.valueOf(resultEntity.getPort()));
            routeZuulVo.setRoutes(JsonUtil.fromJson(resultEntity.getResult(), new TypeReference<List<RouteZuulPo>>() {
            }));
            result.add(routeZuulVo);
        }
        return Result.ok(result);
    }

    @PostMapping("do-list-gateway-names")
    @ResponseBody
    public Result<List<String>> doListGatewayNames(@RequestParam(value = "gatewayName", required = false) String gatewayName) {
        return Result.ok(serviceResource.getGatewayList(RouteZuulService.GATEWAY_TYPE));
    }

    @PostMapping("do-add")
    @ResponseBody
    public Result<?> doAdd(RouteZuulDto routeZuulDto) {
        routeZuulService.insert(routeZuulDto);
        return Result.ok();
    }

    @PostMapping("do-edit")
    @ResponseBody
    public Result<?> doEdit(RouteZuulDto routeZuulDto) {
        routeZuulService.update(routeZuulDto);
        return Result.ok();
    }

    @PostMapping("do-enable")
    @ResponseBody
    public Result<?> doEnable(@RequestParam(value = "id") Long id) {
        routeZuulService.enable(id, true);
        return Result.ok();
    }

    @PostMapping("do-disable")
    @ResponseBody
    public Result<?> doDisable(@RequestParam(value = "id") Long id) {
        routeZuulService.enable(id, false);
        return Result.ok();
    }

    @PostMapping("do-delete")
    @ResponseBody
    public Result<?> doDelete(@RequestParam(value = "ids") String ids) {
        List<Long> idList = CommonTool.parseList(ids, ",", Long.class);
        routeZuulService.logicDelete(new HashSet<>(idList));
        return Result.ok();
    }

    @PostMapping("do-publish")
    @ResponseBody
    public Result<?> doPublish() throws Exception {
        routeZuulService.publish();
        return Result.ok();
    }
}