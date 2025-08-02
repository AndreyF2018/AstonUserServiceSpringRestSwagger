package org.example.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.dto.UserDto;
import org.example.dto.UserMapper;
import org.example.models.User;
import org.example.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Service API")
public class UserController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get user by ID")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UserDto>> findById(@Parameter(description = "User id") @PathVariable int id) {
        Optional<User> user = userService.findById(id);
        return user.map(value -> ResponseEntity.ok(toConcreteModel(value))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation (summary = "Get user by email")
    @GetMapping("/email/{email}")
    public ResponseEntity<EntityModel<UserDto>> findByEmail (@Parameter(description = "User email") @PathVariable String email) {
        Optional<User> user = userService.findByEmail(email);
        return user.map(value -> ResponseEntity.ok(toConcreteModel(value))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get all users")
    @GetMapping
    public CollectionModel<EntityModel<UserDto>> findAll() {
        List<User> users = userService.findAll();
        List<UserDto> usersDto = new ArrayList<>();
        List<EntityModel<UserDto>> models = new ArrayList<>();
        for (User user : users) {
            models.add(toConcreteModel(user));
            //usersDto.add(UserMapper.toDto(user));
        }
        return toGlobalModel(models);
    }

    @Operation(summary = "Create new user")
    @PostMapping
    public ResponseEntity<EntityModel<UserDto>> save(@Parameter(description = "User data") @Valid @RequestBody UserDto userDto) {
        User user = UserMapper.toEntity(userDto);
        User savedUser = userService.save(user);
        if (savedUser != null) {
            logger.debug("User with id = {} saved successfully.", savedUser.getId());
        }
        EntityModel<UserDto> model = toConcreteModel(savedUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @Operation(summary = "Update user")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<UserDto>> update (@Parameter(description = "User id") @PathVariable int id,
                                           @Parameter(description = "User data") @Valid @RequestBody UserDto userDto) {
        User user = UserMapper.toEntity(userDto);
        // Чтобы не создался дубликат
        user.setId(id);
        User updatedUser = userService.update(user);
        if (updatedUser != null) {
            logger.debug("User with id = {} updated successfully.", updatedUser.getId());
        }
        return ResponseEntity.ok(toConcreteModel(updatedUser));
    }

    @Operation(summary = "Delete user")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "User id") @PathVariable int id)
    {
        Optional<User> user = userService.findById(id);
        if(user.isPresent()) {
            userService.delete(user.get());
            logger.debug("User with id = {} was successfully deleted.", id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Добавление ссылок для конкретного пользователя
    private EntityModel<UserDto> toConcreteModel (User user) {
        UserDto userDto = UserMapper.toDto(user);
        EntityModel<UserDto> model = EntityModel.of(userDto);
        model.add(linkTo(methodOn(UserController.class).findById(user.getId())).withSelfRel());
        model.add(linkTo(methodOn(UserController.class).findAll()).withRel("all-users"));
        model.add(linkTo(methodOn(UserController.class).update(user.getId(), userDto)).withRel("update-user"));
        model.add(linkTo(methodOn(UserController.class).save(null)).withRel("create-user"));
        model.add(linkTo(methodOn(UserController.class).delete(user.getId())).withRel("delete-user"));
        return model;
    }

    // Добавление глобальных ссылок для всех пользователей
    private CollectionModel<EntityModel<UserDto>> toGlobalModel (List<EntityModel<UserDto>> models) {
        CollectionModel<EntityModel<UserDto>> collection = CollectionModel.of(models);
        collection.add(linkTo(methodOn(UserController.class).findAll()).withSelfRel());
        collection.add(linkTo(methodOn(UserController.class).save(null)).withRel("create-user"));
        return collection;

    }
}
