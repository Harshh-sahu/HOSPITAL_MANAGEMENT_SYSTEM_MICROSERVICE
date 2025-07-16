package com.hms.user.jwt;

import com.hms.user.dto.UserDTO;
import com.hms.user.exception.HmsException;
import com.hms.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try{
         UserDTO dto = userService.getUser(email);
         return new CustomUserDetails(dto.getId(),
                 dto.getEmail(),
                 dto.getPassword(),
                 dto.getRole(),
                 dto.getName(),
                 dto.getEmail(),
                 null); // Assuming authorities are not needed here, otherwise you can set them accordingly
        }catch (HmsException e){
            // Handle the case where the user is not found

e.printStackTrace();
        }


        return null;
    }
}
