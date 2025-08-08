package com.hms.user.service;

import java.util.Optional;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hms.user.dto.UserDTO;
import com.hms.user.entity.User;
import com.hms.user.exception.HmsException;
import com.hms.user.repository.UserRepository;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private ApiService apiService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public void registerUser(UserDTO userDTO) throws HmsException {


        Optional<User> opt = userRepository.findByEmail(userDTO.getEmail());

        if (opt.isPresent()) {
            throw new HmsException("USER_ALREADY_EXISTS");
        }

        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        Long profileId = apiService.addProfile(userDTO).block();
        System.out.println(profileId);
        userDTO.setProfileId(profileId);
        userRepository.save(userDTO.toEntity());


    }

    @Override
    public UserDTO login(UserDTO userDTO) throws HmsException {
        User user = userRepository.findByEmail(userDTO.getEmail()).orElseThrow(() -> new HmsException("user Not found"));
        if (!passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
            throw new HmsException("INVALID_CREDENTIALS");
        }
        user.setPassword(null); // Clear password before returning

        return user.toDTO();


    }

    @Override
    public UserDTO getUserById(Long userId) throws HmsException {
        return userRepository.findById(userId).orElseThrow(() -> new HmsException("USER_NOT_FOUND")).toDTO();
    }

    @Override
    public void updateUser(UserDTO userDTO) {

    }

    @Override
    public UserDTO getUser(String email) throws HmsException {
        return userRepository.findByEmail(email).orElseThrow(() -> new HmsException("USER_NOT_FOUND")).toDTO();
    }


}
