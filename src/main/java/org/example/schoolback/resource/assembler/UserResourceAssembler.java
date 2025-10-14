package org.example.schoolback.resource.assembler;

import org.example.schoolback.controller.UserController;
import org.example.schoolback.entity.User;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class UserResourceAssembler extends RepresentationModelAssemblerSupport<User, UserResource> {

    public UserResourceAssembler() {
        super(UserController.class, UserResource.class);
    }

    @Override
    public UserResource toModel(User user) {
        UserResource resource = new UserResource();

        resource.setId(user.getId());
        resource.setLogin(user.getLogin());
        resource.setRole(user.getRole());
        resource.setFirstName(user.getFirstName());
        resource.setSecondName(user.getSecondName());
        resource.setPatronymicName(user.getPatronymicName());
        resource.setFullName(user.getFullName());
        resource.setEmail(user.getEmail());
        resource.setBirthDate(user.getBirthDate());
        resource.setCoins(user.getCoins());

        // Добавляем ссылки
        addCommonLinks(resource, user);

        return resource;
    }

    private void addCommonLinks(UserResource resource, User user) {
        resource.add(linkTo(methodOn(UserController.class)
                .getUserById(user.getId()))
                .withSelfRel());
    }
}