package com.example.socialNetwork.demo.services;

import com.example.socialNetwork.demo.dto.UserDTO;
import com.example.socialNetwork.demo.entity.UserA;
import com.example.socialNetwork.demo.entity.enums.ERole;
import com.example.socialNetwork.demo.exceptions.UserExistException;
import com.example.socialNetwork.demo.payload.request.SignupRequest;
import com.example.socialNetwork.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
;

@Service
public class UserService {
    public static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserA createUser(SignupRequest userIn) {
        UserA user = new UserA();
        user.setEmail(userIn.getEmail());
        user.setName(userIn.getFirstname());
        user.setLastname(userIn.getLastname());
        user.setUsername(userIn.getUsername());
        user.setPassword(passwordEncoder.encode(userIn.getPassword())); // кодируем пароль в объект User
        user.getRoles().add(ERole.ROLE_USER);

        try {
            LOG.info("Saving User {}", userIn.getEmail());
            return userRepository.save(user); //сохраняем user в бд,возвращаем user
        } catch (Exception e) {
            LOG.error("Error during registration. {}", e.getMessage());
            throw new UserExistException("The user " + user.getUsername() + " already exist. Please check credentials");
        }
    }

    public UserA updateUser(UserDTO userDTO, Principal principal) {
        UserA user = getUserByPrincipal(principal);
        user.setName(userDTO.getFirstname());
        user.setLastname(userDTO.getLastname());
        user.setBio(userDTO.getBio());

        return userRepository.save(user);
    }

    public UserA getCurrentUser(Principal principal) {
        return getUserByPrincipal(principal);
    }

    private UserA getUserByPrincipal(Principal principal) {
        String username = principal.getName();
        return userRepository.findUserAByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found with username " + username));

    }

    public UserA getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
