package com.hms.user.api;

import com.hms.user.clients.Profile;
import com.hms.user.dto.LoginDTO;
import com.hms.user.dto.ResponseDTO;
import com.hms.user.jwt.JwtUtil;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.hms.user.dto.UserDTO;
import com.hms.user.exception.HmsException;
import com.hms.user.service.UserService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/user")
@Validated
@CrossOrigin
public class UserAPI {

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> registerUser(@RequestBody @Valid UserDTO userDTO)throws HmsException {
        userService.registerUser(userDTO);

        return new ResponseEntity<>(new ResponseDTO("Account created."),HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginDTO loginDTO) throws HmsException {
try{
    authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    loginDTO.getEmail(),
                    loginDTO.getPassword()
            )
    );

}catch (AuthenticationException e){
    throw  new HmsException("INVALID_CREDENTIALS");
}

final UserDetails userDetails = userDetailsService.loadUserByUsername(loginDTO.getEmail());
final String jwt = jwtUtil.generateToken(userDetails);

return new ResponseEntity<>(jwt,HttpStatus.OK);
    }


@GetMapping("/test")
    public ResponseEntity<String> test(){
        return new ResponseEntity<>("Test",HttpStatus.OK);
}



@GetMapping("/getProfile/{id}")
    public ResponseEntity<Long> getProfile(@PathVariable Long id) throws HmsException{
        return new ResponseEntity<>(userService.getProfile(id),HttpStatus.OK);
}
}
