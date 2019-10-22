package com.springcloud.service.client.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;


/**
 * Title:
 * Description:
 *
 * @author 谭 tmn
 * @email AbelEthan@126.com
 * @date 2018/10/25 23:40
 */
public class UserRibbonClientHystrixCommand extends HystrixCommand<List> {

    private final RestTemplate restTemplate;
    private final String providerServiceName;

    public UserRibbonClientHystrixCommand(RestTemplate restTemplate, String providerServiceName) {
        super(HystrixCommandGroupKey.Factory.asKey(
                "User-Ribbon-Client"),
                100
        );
        this.restTemplate = restTemplate;
        this.providerServiceName = providerServiceName;
    }

    /**
     * 主逻辑实现
     *
     * @return
     * @throws Exception
     */
    @Override
    protected List run() throws Exception {
        return restTemplate.getForObject("http://" + providerServiceName + "/user/list", List.class);
    }

    /**
     * Fallback 实现
     *
     * @return 空集合
     */
    @Override
    protected List getFallback() {
        return Collections.emptyList();
    }
}
