package com.springcloud.service.client.rule;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.Server;

import java.util.List;

/**
 * Title: 自定义{@link IRule} 实现
 * Description:
 *
 * @author 谭 tmn
 * @email AbelEthan@126.com
 * @date 2018/10/24 15:26
 */
public class ClientRule extends AbstractLoadBalancerRule {
    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {

    }

    @Override
    public Server choose(Object o) {
        ILoadBalancer iLoadBalancer = getLoadBalancer();

        //获取所有可达的服务列表
        List<Server> servers = iLoadBalancer.getReachableServers();
        if (servers.isEmpty()){
            return null;
        }

        //永远选择最后一台服务器
        Server targetServer = servers.get(servers.size() - 1);
        return targetServer;
    }
}
