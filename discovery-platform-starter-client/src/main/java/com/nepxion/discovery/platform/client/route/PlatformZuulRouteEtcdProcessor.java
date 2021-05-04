package com.nepxion.discovery.platform.client.route;

/**
 * <p>Title: Nepxion Discovery</p>
 * <p>Description: Nepxion Discovery</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.nepxion.discovery.common.constant.DiscoveryConstant;
import com.nepxion.discovery.common.etcd.proccessor.EtcdProcessor;
import com.nepxion.discovery.platform.client.constant.PlatformConstant;
import com.nepxion.discovery.plugin.strategy.zuul.route.ZuulStrategyRoute;

public class PlatformZuulRouteEtcdProcessor extends EtcdProcessor {
    @Value("${" + DiscoveryConstant.SPRING_APPLICATION_NAME + "}")
    private String dataId;

    @Autowired
    private ZuulStrategyRoute zuulStrategyRoute;

    @Override
    public String getGroup() {
        return PlatformConstant.GROUP;
    }

    @Override
    public String getDataId() {
        return dataId;
    }

    @Override
    public String getDescription() {
        return PlatformConstant.ZUUL_ROUTE_DESCRIPTION;
    }

    @Override
    public void callbackConfig(String config) {
        zuulStrategyRoute.updateAll(config);
    }
}