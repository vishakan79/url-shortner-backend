package Demo.project.of.sprinboot.Service;

import Demo.project.of.sprinboot.DTO.LoginRequest;
import Demo.project.of.sprinboot.Model.User;
import Demo.project.of.sprinboot.Resposiory.UserResposiory;
import Demo.project.of.sprinboot.SecurityJWT.JWTUtils;
import Demo.project.of.sprinboot.SecurityJWT.JwtAuthenticationFilter;
import Demo.project.of.sprinboot.SecurityJWT.JwtTokenResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class Userservice {
    private PasswordEncoder passwordEncoder;
    private UserResposiory  userResposiory;
    private AuthenticationManager authenticationManager;

    private JWTUtils  jwtUtils;


    public JwtTokenResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsimp userDetailsimp = (UserDetailsimp) authentication.getPrincipal();
        String jwt = jwtUtils.generateJWT(userDetailsimp);
        return  new JwtTokenResponse(jwt);
    }

    public User  registerUser(User user){
        if (user.getUsername() == null || user.getUsername().isBlank()
                || user.getPassword() == null || user.getPassword().length() < 6) {
            throw new IllegalArgumentException("Username and a password of at least 6 characters are required");
        }
        if (userResposiory.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalStateException("Username already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userResposiory.save(user);
    }
    public User  findbyusername(String username){
        return userResposiory.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Username not found" + username)
        );
    }
}
