package com.example.springwebflux.test;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.LoopResources;
import reactor.netty.tcp.TcpClient;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class App3 {

  public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {

    TcpClient tcpClient = TcpClient.create()
        .runOn(LoopResources.create("some-event-loop", 1, 1, true));

    WebClient wc = WebClient.builder()
        .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
        .baseUrl("http://127.0.0.1:8083/hello")
        .build();



    long time = System.currentTimeMillis();

    for (int i = 0; i < 50; i++) {

      System.out.println("++++++++++++++++");
      wc.get().exchange().map(v->{
        System.out.println(v.bodyToMono(String.class));
        return v;
      }).block();
      /*wc.post().body(BodyInserters.fromFormData("aa", "bb")).exchange().map(v->{

        System.out.println(v.statusCode());
        return v;
      }).block();*/

      System.out.println("------------");

    }

    System.out.println("time: " + (System.currentTimeMillis() - time));
    //time: 1943
  }
}