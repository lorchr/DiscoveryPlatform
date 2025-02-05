package com.nepxion.discovery.platform.server.controller;

/**
 * <p>Title: Nepxion Discovery</p>
 * <p>Description: Nepxion Discovery</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 *
 * @author Ning Zhang
 * @version 1.0
 */

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nepxion.discovery.common.entity.InstanceEntity;
import com.nepxion.discovery.platform.server.adapter.PlatformDiscoveryAdapter;
import com.nepxion.discovery.platform.server.entity.base.BaseStateEntity;
import com.nepxion.discovery.platform.server.entity.dto.BlueGreenDto;
import com.nepxion.discovery.platform.server.entity.po.BlueGreenPo;
import com.nepxion.discovery.platform.server.entity.po.ListSearchNamePo;
import com.nepxion.discovery.platform.server.entity.response.Result;
import com.nepxion.discovery.platform.server.service.BlueGreenService;
import com.nepxion.discovery.platform.server.tool.CommonTool;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@Api("蓝绿发布相关接口")
@RestController
@RequestMapping(BlueGreenController.PREFIX)
public class BlueGreenController {
    public static final String PREFIX = "blue-green";

    @Autowired
    private BlueGreenService blueGreenService;
    @Autowired
    private PlatformDiscoveryAdapter platformDiscoveryAdapter;

    @ApiOperation("获取蓝绿发布信息列表")
    @PostMapping("do-list")
    public Result<List<BlueGreenDto>> doList(ListSearchNamePo listSearchNamePo) {
        IPage<BlueGreenDto> blueGreenDtoPage = blueGreenService.page(listSearchNamePo.getName(), listSearchNamePo.getPage(), listSearchNamePo.getLimit());
        return Result.ok(blueGreenDtoPage.getRecords(), blueGreenDtoPage.getTotal());
    }

    @ApiOperation("通过服务名称获取所有该服务的信息")
    @PostMapping("do-list-service-metadata")
    public Result<List<InstanceEntity>> doListServiceMetadata(@RequestParam("serviceName") String serviceName) {
        if (StringUtils.isEmpty(serviceName)) {
            return Result.ok(new ArrayList<>());
        }
        return Result.ok(platformDiscoveryAdapter.getInstanceList(serviceName));
    }

    @ApiOperation("获取所有入口的名称")
    @PostMapping("do-list-portal-names")
    public Result<List<String>> doListPortalNames(@RequestParam(name = "portalName", defaultValue = "") String portalName, @RequestParam("portalType") Integer portalTypeInt) {
        BaseStateEntity.PortalType portalType = BaseStateEntity.PortalType.get(portalTypeInt);
        List<String> result = new ArrayList<>();
        switch (Objects.requireNonNull(portalType)) {
            case GATEWAY:
                result.addAll(platformDiscoveryAdapter.getGatewayNames());
                break;
            case SERVICE:
                result.addAll(platformDiscoveryAdapter.getServiceNames());
                break;
            case GROUP:
                result.addAll(platformDiscoveryAdapter.getGroupNames());
                break;
        }
        List<String> portNameList = blueGreenService.listPortalNames();
        result.removeAll(portNameList);
        if (StringUtils.isNotEmpty(portalName)) {
            result.add(portalName);
        }
        return Result.ok(result.stream().distinct().sorted(Comparator.naturalOrder()).collect(Collectors.toList()));
    }

    @ApiOperation("获取所有服务的名称")
    @PostMapping("do-list-service-names")
    public Result<List<String>> doListServiceNames() {
        return Result.ok(platformDiscoveryAdapter.getServiceNames());
    }

    @ApiOperation("校验自定义表达式")
    @PostMapping("validate-expression")
    public Result<Boolean> validateExpression(@RequestParam("expression") String expression, @RequestParam("validation") String validation) {
        return Result.ok(platformDiscoveryAdapter.validateExpression(expression, validation));
    }


    @ApiOperation("新增蓝绿信息")
    @PostMapping("do-insert")
    public Result<Boolean> doInsert(BlueGreenPo blueGreenPo) {
        return Result.ok(blueGreenService.insert(blueGreenPo));
    }

    @ApiOperation("修改蓝绿信息")
    @PostMapping("do-update")
    public Result<?> doUpdate(BlueGreenPo blueGreenPo) {
        blueGreenService.update(blueGreenPo);
        return Result.ok();
    }

    @ApiOperation("启用蓝绿")
    @ApiImplicitParam(name = "id", value = "蓝绿id", required = true, dataType = "String")
    @PostMapping("do-enable")
    public Result<?> doEnable(@RequestParam(value = "id") Long id) {
        blueGreenService.enable(id, true);
        return Result.ok();
    }

    @ApiOperation("禁用蓝绿")
    @ApiImplicitParam(name = "id", value = "蓝绿id", required = true, dataType = "String")
    @PostMapping("do-disable")
    public Result<?> doDisable(@RequestParam(value = "id") Long id) {
        blueGreenService.enable(id, false);
        return Result.ok();
    }

    @ApiOperation("删除蓝绿")
    @ApiImplicitParam(name = "ids", value = "蓝绿id, 多个用逗号分隔", required = true, dataType = "String")
    @PostMapping("do-delete")
    public Result<?> doDelete(@RequestParam(value = "ids") String ids) {
        List<Long> idList = CommonTool.parseList(ids, ",", Long.class);
        blueGreenService.logicDelete(new HashSet<>(idList));
        return Result.ok();
    }

    @ApiOperation("发布蓝绿发布")
    @PostMapping("do-publish")
    public Result<?> doPublish() throws Exception {
        blueGreenService.publish();
        return Result.ok();
    }
}