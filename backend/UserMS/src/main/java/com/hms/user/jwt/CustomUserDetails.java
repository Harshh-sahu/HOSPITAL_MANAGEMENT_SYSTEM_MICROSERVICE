package com.hms.user.jwt;

import com.hms.user.dto.Roles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {


    private Long id;
    private String username;
    private String password;
    private Roles role;
    private String name;
    private String email;
    private Long profileId;
    private Collection<? extends GrantedAuthority> authorities;


}
