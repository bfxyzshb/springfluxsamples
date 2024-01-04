package com.example.spring.test.refreshscope;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * @ClassName TestService
 * @Description TODO
 * @Author hebiao1
 * @Date 2024/1/4 10:53
 * @Version 1.0
 */
//@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
@RefreshScope
@Component
public class TestService {
    @Value("${service_value:50}")
    private String value;

    public String say(){
        System.out.println("hell test service:"+value);
        return value;
    }
}

