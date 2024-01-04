package com.example.spring.test.refreshscope;

import com.example.spring.test.beanTest.MyEnvironmentPostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AwareTestController {
    
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private TestService service;
    @Autowired
    private MyEnvironmentPostProcessor myEnvironmentPostProcessor;
 
    @GetMapping("/testPrototype")
    public String testPrototype() {
        TestService testService = applicationContext.getBean(TestService.class);
        System.out.println(testService);
        return testService.toString();
    }

    @GetMapping("/testAutowired")
    public String testAutowired() {
        System.out.println(service);

        return service.say();
    }

    /**
     * 模拟更新bean的属性值
     * 刷新bean,重新生成新的bean
     */
    @Autowired
    RefreshScope refreshScope;
    @GetMapping("/refresh")
    public String  refresh(@RequestParam Integer value) {
        //applicationContext.publishEvent(new RefreshScopeRefreshedEvent());
        //refreshScope.refreshAll();
        //模拟更新配置
        myEnvironmentPostProcessor.setSources(value);
        //刷新bean
        refreshScope.refresh(TestService.class);
        return "ok";
    }
}
