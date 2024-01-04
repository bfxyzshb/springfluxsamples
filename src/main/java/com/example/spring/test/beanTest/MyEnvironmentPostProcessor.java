package com.example.spring.test.beanTest;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName MyEnvironmentPostProcessor
 * @Description TODO
 * @Author hebiao1
 * @Date 2024/1/3 17:42
 * @Version 1.0
 */
@Component
public class MyEnvironmentPostProcessor implements EnvironmentPostProcessor, EnvironmentAware, BeanNameAware {
    Map<String, Object> sources =new HashMap<>();
    ConfigurableEnvironment environment;

    private String beanName;


    public void setSources(Integer value){
        MutablePropertySources propertySources = environment.getPropertySources();
        MapPropertySource fizzPropertySource = new MapPropertySource("AfterEnv", sources);
        propertySources.addFirst(fizzPropertySource);
        sources.put("service_value",value);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment=(ConfigurableEnvironment)environment;
    }

    @Override
    public void setBeanName(String s) {
        this.beanName = s;
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {

    }
}
