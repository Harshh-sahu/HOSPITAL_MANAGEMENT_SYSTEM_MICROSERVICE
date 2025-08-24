package com.hms.appointment.service;


import com.hms.appointment.dto.DoctorDTO;
import com.hms.appointment.dto.PatientDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ApiService {

    @Autowired
    private WebClient.Builder webclient;



    public Mono<Boolean> doctorExist(Long id){



        return webclient.build().get()
                .uri("http://localhost:9100/profile/doctor/exists/" + id)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<Boolean> patientExist(Long id){



        return webclient.build().get()
                .uri("http://localhost:9100/profile/patient/exists/" + id)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

public Mono<PatientDTO> getPatientById(Long id) {
    return webclient.build()
            .get()
            .uri("http://localhost:9100/profile/patient/get/" + id)
            .retrieve()
            .bodyToMono(PatientDTO.class);


}
public Mono<DoctorDTO> getDoctorById(Long id) {
    return webclient.build()
            .get()
            .uri("http://localhost:9100/profile/doctor/get/" + id)
            .retrieve()
            .bodyToMono(DoctorDTO.class);
}
}