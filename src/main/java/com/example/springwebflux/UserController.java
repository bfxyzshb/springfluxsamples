package com.example.springwebflux;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

@RestController
public class UserController {
    //WebClient webClient = WebClient.create("http://xxx");
    @Resource
    WebClient webClient;
    @Autowired
    private UserService userService;

    @GetMapping("/user/{id}")
    public Mono<User> getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @GetMapping("/user")
    public Flux<User> getAllUser() {
        return userService.getAllUser();
    }

    @PostMapping("/saveuser")
    public Mono<Void> saveUser(@RequestBody User user) {
        Mono<User> userMono = Mono.just(user);
        return userService.saveUser(userMono);
    }
    String ipPorts = "";
    Random random = new Random();
    List<String> ipPort = Arrays.asList(ipPorts.split(","));
    @GetMapping("/test")
    public Mono Test(@RequestParam int sleep, @RequestParam int length, ServerHttpResponse serverHttpResponse) {

        List<Mono> monos = new ArrayList<>();
        monos.add(Mono.just(1).flatMap(v -> {
            /*Mono<ClientHttpResponse> mapMono = webClient.get()
                    .uri("/mock?sleep=" + sleep + "&length=" + length)
                    .accept(MediaType.APPLICATION_JSON)*/
            WebClient.RequestBodyUriSpec requestBodyUriSpec = webClient.method(HttpMethod.GET);
            WebClient.RequestBodySpec req = requestBodyUriSpec.accept(MediaType.APPLICATION_JSON);
            requestBodyUriSpec.uri("http://" + ipPort.get(random.nextInt(15)) +"/mock?sleep=" + sleep + "&length=" + length);
            Mono<ClientResponse> responseMono = req.exchange();
            return responseMono.flatMap(response -> response.bodyToMono(String.class).flatMap(s1 -> Mono.just(JSON.parseObject(s1))));
        }));
        monos.add(Mono.just(2).flatMap(v -> {
            WebClient.RequestBodyUriSpec requestBodyUriSpec = webClient.method(HttpMethod.GET);
            WebClient.RequestBodySpec req = requestBodyUriSpec.accept(MediaType.APPLICATION_JSON);
            requestBodyUriSpec.uri("http://" + ipPort.get(random.nextInt(15)) +"/mock?sleep=" + sleep + "&length=" + length);
            Mono<ClientResponse> responseMono = req.exchange();
            return responseMono.flatMap(response -> response.bodyToMono(String.class).flatMap(s1 -> Mono.just(JSON.parseObject(s1))));
        }));

        Mono<JSONObject>[] monoArray = monos.stream().toArray(Mono[]::new);
        JSONObject jsonObject = new JSONObject();
        //使用的是webclient的netty的工作线程
        Mono<JSONObject> mono = Flux.merge(monoArray).subscribeOn(Schedulers.elastic()).reduce((v1, v2) -> {
            System.out.println(Thread.currentThread());
            Integer value0 = v1.getIntValue("mock0");
            Integer value1 = v2.getIntValue("mock0");
            jsonObject.put("val0", value0);
            jsonObject.put("val1", value1);
            return jsonObject;
        });
        //使用的是springboot的netty的工作线程
        System.out.println(Thread.currentThread());
        return mono;
    }


    public static Scheduler MyScheduler() {
        Executor executor = new ThreadPoolExecutor(
                5000,  //corePoolSize
                6000,  //maximumPoolSize
                0L, TimeUnit.MILLISECONDS, //keepAliveTime, unit
                new LinkedBlockingQueue<>(),  //workQueue
                Executors.defaultThreadFactory()
        );
        return Schedulers.fromExecutor(executor);
    }


}