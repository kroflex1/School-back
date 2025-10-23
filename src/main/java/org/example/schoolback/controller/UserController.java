package org.example.schoolback.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.example.schoolback.entity.User;
import org.example.schoolback.dto.UserDTO;
import org.example.schoolback.util.assembler.impl.UserResourceAssembler;
import org.example.schoolback.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserResourceAssembler userResourceAssembler;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить информацию о пользователе по его id")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        UserDTO resource = userResourceAssembler.toModel(user);
        return ResponseEntity.ok(resource);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Создать нового пользователя")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        final User createdUser = userService.createUser(userDTO, resource -> userResourceAssembler.createEntity(resource));
        final UserDTO resource = userResourceAssembler.toModel(createdUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Обновить данные пользователя")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        final User createdUser = userService.updateUser(id, userDTO, (userEntity, resource) -> userResourceAssembler.updateEntity(userEntity, resource));
        final UserDTO resource = userResourceAssembler.toModel(createdUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удалить пользователя")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Получить информацию о своём профиле")
    public ResponseEntity<UserDTO> getCurrentUser() {
        Optional<User> user = userService.getCurrentUser();
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserDTO resource = userResourceAssembler.toModel(user.get());
        return ResponseEntity.ok(resource);
    }
}
