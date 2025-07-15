package org.example.controllers;

import org.example.dto.UserDto;
import org.example.dto.UserMapper;
import org.example.models.User;
import org.example.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findById(@PathVariable int id) {
        Optional<User> user = userService.findById(id);
        if(user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(UserMapper.toDto(user.get()));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto> findByEmail (@PathVariable String email) {
        Optional<User> user = userService.findByEmail(email);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(UserMapper.toDto(user.get()));
    }

    @GetMapping
    public List<UserDto> findAll() {
        List<User> users = userService.findAll();
        List<UserDto> usersDto = new ArrayList<>();
        for (User user : users) {
            usersDto.add(UserMapper.toDto(user));
        }
        return usersDto;
    }

    public void save(User user) {
        userService.save(user);
    }

    public void update(User user) {
        userService.update(user);
    }

    public void delete(User user) {
        userService.delete(user);
    }
}
