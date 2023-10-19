package com.example.springwebflux;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {

    public Mono<User> getUserById(Integer id);

    public Flux<User> getAllUser();

    public Mono<Void> saveUser(Mono<User> userMono);
}
