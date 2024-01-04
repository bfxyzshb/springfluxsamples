package com.example.springwebflux;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.springwebflux.test.Test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.time.Duration;
import java.time.chrono.ThaiBuddhistEra;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@RestController
public class UserController {
    private static final Log log = LogFactory.getLog(UserController.class);
    //WebClient webClient = WebClient.create("http://demo.starry.intra.weibo.com");
    ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    @PostConstruct
    private void init() {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
            ThreadInfo[] threadInfo = threadBean.dumpAllThreads(false, false);
            List<ThreadInfo> threadInfos = Arrays.asList(threadInfo);
            Map<String, List<ThreadInfo>> map = threadInfos.stream().collect(Collectors.groupingBy(t -> {
                return t.getThreadName().replaceAll("[0-9]", "");
            }));
            map.forEach((k, v) -> {
                log.info("thread:" + k + "===:" + v.size());
                //System.out.println(v.size());
            });
            log.info("===========================all:" + threadInfos.size());
        }, 1, 5, TimeUnit.SECONDS);
    }


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
        executors.submit(() -> {

        });
        return userService.getAllUser();
    }

    @PostMapping("/saveuser")
    public Mono<Void> saveUser(@RequestBody User user) {
        Mono<User> userMono = Mono.just(user);
        return userService.saveUser(userMono);
    }

    String ipPorts = "10.182.30.137:8088,10.182.30.143:8088,10.182.30.144:8088,10.182.30.146:8088,10.182.30.149:8088,10.182.30.150:8088,10.182.30.151:8088,10.182.30.157:8088,10.182.30.105:8088,10.182.30.156:8088,10.182.30.158:8088,10.182.30.134:8088,10.182.30.113:8088,10.182.30.106:8088,10.182.30.136:8088";
    Random random = new Random();
    List<String> ipPort = Arrays.asList(ipPorts.split(","));
    ExecutorService executors = new ThreadPoolExecutor(500, 1000,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(100));
    Scheduler scheduler = Schedulers.newElastic("test-elastic", 1);
    //executors = Executors.newVirtualThreadPerTaskExecutor();

    @GetMapping("/test")
    public Mono Test(@RequestParam int sleep, @RequestParam int length, ServerHttpResponse serverHttpResponse, @RequestParam boolean flag) {
        //sleep = random.nextInt(10000);
        List<Mono> monos = new ArrayList<>();
        int finalSleep = sleep;
        System.out.println(finalSleep);
        for(int i=0;i<1;i++){
            int finalI = i;
            monos.add(Mono.just(1).flatMap(v -> {
                System.out.println("webclient:" + Thread.currentThread());
                WebClient.RequestBodyUriSpec requestBodyUriSpec = webClient.method(HttpMethod.GET);
                WebClient.RequestBodySpec req = requestBodyUriSpec.accept(MediaType.APPLICATION_JSON);
                //demo.starry.intra.weibo.com
                //requestBodyUriSpec.uri("http://" + ipPort.get(random.nextInt(15)) + "/mock?sleep=" + sleep + "&length=" + length);
                if (flag) {
                    //requestBodyUriSpec.uri("http://demo.starry.intra.weibo.com/mock?sleep=" + finalSleep + "&length=" + length);
                    requestBodyUriSpec.uri("http://" + ipPort.get(random.nextInt(15)) + "/mock?sleep=" + sleep + "&length=" + length);
                } else {
                    requestBodyUriSpec.uri("http://10.235.64.14:8083/hello");
                }
                Mono<ClientResponse> responseMono = req.exchange();
                return responseMono.flatMap(response -> {
                    System.out.println("webclient response:"+ finalI + Thread.currentThread());
                    return response.bodyToMono(String.class)/*.publishOn(Schedulers.fromExecutor(executors))*/.flatMap(s1 ->{
                        System.out.println("response decode:"+Thread.currentThread());
                        return Mono.just(JSON.parseObject(s1));
                    });
                });
            }));
        }

      /*  monos.add(Mono.just("1").flatMap(v->{
            System.out.println("merge1=====" + Thread.currentThread());
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return Mono.just(JSON.parseObject("{\"mock0\":1}"));
        }));
        monos.add(Mono.just("2").flatMap(v->{
            System.out.println("merge2=====" + Thread.currentThread());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return Mono.just(JSON.parseObject("{\"mock0\":1}"));
        }));*/
        Mono<JSONObject>[] monoArray = monos.stream().toArray(Mono[]::new);
        JSONObject jsonObject = new JSONObject();
        //使用的是webclient的netty的工作线程
        Mono<JSONObject> mono = Flux.merge(monoArray)/*.subscribeOn(Schedulers.fromExecutor(executors))*/.flatMap(v -> {
            System.out.println("merge======" + Thread.currentThread());
            return Flux.just(v);
        }).timeout(Duration.ofMillis(30000)).reduce((v1, v2) -> {
            System.out.println("reduce=======" + Thread.currentThread());
            Integer value0 = v1.getIntValue("mock0");
            Integer value1 = v2.getIntValue("mock0");
            jsonObject.put("val0", value0);
            jsonObject.put("val1", value1);
            for (int i = 0; i < 10000; i++) {
                "aaabb".matches("a");
            }
            return jsonObject;
        }).doOnError(e -> {
                    System.out.println("time====" + Thread.currentThread());
                    //log.info("=======>>>>>>>", e);
                }
        );
        //使用的是springboot的netty的工作线程
        System.out.println(Thread.currentThread());
        return mono;
    }


    Executor executor = new ThreadPoolExecutor(
            5000,  //corePoolSize
            6000,  //maximumPoolSize
            0L, TimeUnit.MILLISECONDS, //keepAliveTime, unit
            new LinkedBlockingQueue<>(),  //workQueue
            Executors.defaultThreadFactory()
    );


}