package com.example.springwebflux;

import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Repository
public class UserServiceImpl implements UserService {
    private final Map<Integer, User> users = new HashMap<>();

    public UserServiceImpl(){
        this.users.put(1, new User("lucy","man",20));
        this.users.put(2, new User("Mary","nv",30));
        this.users.put(3, new User("jack","nv",50));
    }
    @Override
    public Mono<User> getUserById(Integer id) {
        return Mono.just(users.get(id));
    }

    @Override
    public Flux<User> getAllUser() {
        return Flux.fromIterable(users.values());
    }

    @Override
    public Mono<Void> saveUser(Mono<User> userMono) {

        return userMono.doOnNext(person->{
            int id = users.size() + 1;
            users.put(id, person);

        }).thenEmpty(Mono.empty());
    }
}
