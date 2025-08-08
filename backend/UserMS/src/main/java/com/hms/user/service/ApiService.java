package com.hms.user.service;

import com.hms.user.dto.Roles;
import com.hms.user.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ApiService {

    @Autowired
private WebClient.Builder webclient;



public Mono<Long> addProfile(UserDTO userDTO){
    if(userDTO.getRole().equals(Roles.DOCTOR)){
        return webclient.build()
                .post()
                .uri("http://localhost:9100/profile/doctor/add")
                .bodyValue(userDTO)
                .retrieve()
                .bodyToMono(Long.class);

    } else if(userDTO.getRole().equals(Roles.PATIENT)){
        return webclient.build()
                .post()
                .uri("http://localhost:9100/profile/patient/add")
                .body(Mono.just(userDTO), UserDTO.class)
                .retrieve()
                .bodyToMono(Long.class);


    }
    return Mono.error(new IllegalArgumentException("Invalid role: " + userDTO.getRole()));


}




}
