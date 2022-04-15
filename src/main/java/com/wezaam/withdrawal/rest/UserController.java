package com.wezaam.withdrawal.rest;

import com.wezaam.withdrawal.model.User;
import com.wezaam.withdrawal.repository.UserRepository;
import io.swagger.annotations.Api;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/find-all-users")
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @GetMapping("/find-user-by-id/{id}")
    public User findById(@PathVariable Long id) {
        return userRepository.findById(id).orElseThrow();
    }
}
