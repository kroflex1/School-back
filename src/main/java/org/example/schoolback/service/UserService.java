package org.example.schoolback.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.example.schoolback.dto.UserDTO;
import org.example.schoolback.entity.User;
import org.example.schoolback.repository.UserRepository;
import org.example.schoolback.util.Updater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepository;

    private static final EmailValidator EMAIL_VALIDATOR = EmailValidator.getInstance();

    @Transactional(readOnly = true)
    public Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            return userRepository.findByLogin(authentication.getName());
        }
        return Optional.empty();
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }


    public User createUser(UserDTO userDTO, Converter<UserDTO, User> converter) {
        final User newUser = converter.convert(userDTO);
        validateCreate(newUser);
        newUser.setCoins(0L);

        return userRepository.save(newUser);
    }

    public User updateUser(Long userId, UserDTO userDTO, Updater<User, UserDTO> updater) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new IllegalArgumentException(String.format("User not found with id: %d", userId));
        }

        final User existUser = user.get();
        updater.update(existUser, userDTO);
        validateUpdate(userId, existUser);

        return userRepository.save(existUser);
    }

    public void deleteUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new IllegalArgumentException(String.format("User not found with id: %d", userId));
        }
        userRepository.delete(user.get());
    }

    private void validateCreate(User user) {
        validateCommonFields(user);

        if (userRepository.findByLogin(user.getLogin()).isPresent()) {
            throw new IllegalArgumentException(String.format("User with login %s already exists", user.getLogin()));
        }
    }

    private void validateUpdate(Long userId, User updatedUser) {
        final User existUser = userRepository.findById(userId).get();
        validateCommonFields(updatedUser);

        //если в рамках обновления был изменён логин, то мы проверяем, есть ли в бд уже пользователь с таким логином
        if (!existUser.getLogin().equals(updatedUser.getLogin()) && userRepository.findByLogin(updatedUser.getLogin()).isPresent()) {
            throw new IllegalArgumentException(String.format("User with login %s already exists", updatedUser.getLogin()));
        }
    }


    private void validateCommonFields(User user) {
        if (StringUtils.isBlank(user.getLogin())) {
            throw new IllegalArgumentException("Login cannot be empty");
        }
        // Регулярное выражение: только латинские буквы (любой регистр) и цифры
        String regex = "^[a-zA-Z0-9]+$";
        if (!user.getLogin().matches(regex)) {
            throw new IllegalArgumentException("Invalid format of login");
        }

        if (StringUtils.isBlank(user.getPassword()) || user.getPassword().length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters long");
        }

        if (!StringUtils.isBlank(user.getEmail()) && !EMAIL_VALIDATOR.isValid(user.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (user.getRole() == null) {
            throw new IllegalArgumentException("Role cannot be empty");
        }

        if (StringUtils.isBlank(user.getFirstName())) {
            throw new IllegalArgumentException("First name cannot be empty");
        }
        if (StringUtils.isBlank(user.getSecondName())) {
            throw new IllegalArgumentException("Second name cannot be empty");
        }
    }
}
