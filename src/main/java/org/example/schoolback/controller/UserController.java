package org.example.schoolback.controller;

import org.example.schoolback.entity.User;
import org.example.schoolback.resource.assembler.UserResource;
import org.example.schoolback.resource.assembler.UserResourceAssembler;
import org.example.schoolback.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserResourceAssembler userResourceAssembler;


    @GetMapping("/{id}")
    public ResponseEntity<UserResource> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        UserResource resource = userResourceAssembler.toModel(user);
        return ResponseEntity.ok(resource);
    }
}
