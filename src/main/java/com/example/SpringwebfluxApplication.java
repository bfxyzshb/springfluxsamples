package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@SpringBootApplication
@RestController
public class SpringwebfluxApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringwebfluxApplication.class, args);
    }

    // curl http://localhost:8080/getpet
    @GetMapping("/getpet")
    public Mono<Pet> getPet(){
        Mono<Pet> defer = Mono.defer(() -> {
            System.out.println(Thread.currentThread());
            sleep(5000);
            return Mono.just(pet1());
        });
        System.out.println("exit function");
        return defer;
    }

    // curl http://localhost:8080/getpet2
    @GetMapping("/getpet2")
    public Mono<Pet> getPet2(){
        Mono<Pet> defer=WebClient
                .create("http://localhost:9999/getpet")
                .get()
                .retrieve()
                .bodyToMono(Pet.class);
        System.out.println("exit getpet2 function");

        return defer;

    }

    // curl http://localhost:8080/getpets
    @GetMapping("/getpets")
    public Flux<Pet> getPets(){
        Flux<Pet> defer = Flux.defer(() -> {
            sleep(1000);
            return Flux.fromArray(new Pet[]{pet1(), pet2(),pet2(),pet2(),pet2(),pet2()});
        });
        System.out.println("exit function");
        return defer;
    }

    // curl http://localhost:8080/getpet2
    @GetMapping("/getpets2")
    public Flux<Pet> getPets2(){
        return WebClient
                .create("http://localhost:8080/getpets")
                .get()
                .retrieve()
                .bodyToFlux(Pet.class);
    }


    private Pet pet1() {
        return new Pet("pet1", GENDER.MALE);
    }

    private Pet pet2() {
        return new Pet("pet2", GENDER.FEMALE);
    }

    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

class Pet{
    public String name;
    public GENDER gender;

    public Pet() {}

    public Pet(String name, GENDER gender) {
        this.name = name;
        this.gender = gender;
    }
}

enum GENDER{
    MALE,
    FEMALE
}
