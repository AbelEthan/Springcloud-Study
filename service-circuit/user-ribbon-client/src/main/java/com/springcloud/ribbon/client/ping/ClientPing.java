package com.springcloud.ribbon.client.ping;

import com.netflix.loadbalancer.IPing;
import com.netflix.loadbalancer.Server;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Title: 实现 {@link IPing} 接口
 * Description:  检查对象 /health
 *
 * @author 谭 tmn
 * @email AbelEthan@126.com
 * @date 2018/10/25 10:01
 */
public class ClientPing implements IPing {
    @Override
    public boolean isAlive(Server server) {
        String host = server.getHost();
        int port = server.getPort();
        // /health endpoint
        //通过 Spring 组建来实现URL 拼装
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance();
        builder.scheme("http");
        builder.host(host);
        builder.port(port);
        builder.path("/health");

        URI uri = builder.build().toUri();
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity responseEntity = restTemplate.getForEntity(uri, String.class);
        //当响应状态等于 200 时， 返回 true ， 否则 false
        return HttpStatus.OK.equals(responseEntity.getStatusCode());
    }
}
