package com.hms.user.clients;

import com.hms.user.config.FeignClientInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import com.hms.user.dto.UserDTO;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "profileMS",configuration = FeignClientInterceptor.class)
public interface ProfileClient {

    @PostMapping("/profile/doctor/add")
    Long addDoctor(@RequestBody UserDTO userDTO);

    @PostMapping("/profile/patient/add")
    Long addPatient(@RequestBody UserDTO userDTO);

    @GetMapping("/profile/doctor/getProfileId/{id}")
    Long getDoctor(@PathVariable Long id);

    @GetMapping("/profile/patient/getProfileId/{id}")
    Long getPatient(@PathVariable Long id);

}