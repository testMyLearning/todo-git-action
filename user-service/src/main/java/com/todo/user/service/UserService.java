package com.todo.user.service;


import com.todo.common.dto.AuthResponse;
import com.todo.common.dto.UserDto;

import com.todo.common.security.JwtService;
import com.todo.user.entity.User;
import com.todo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервис для работы с пользователями
 *
 * Зачем:
 * 1. Бизнес-логика пользователей
 * 2. Преобразование Entity ↔ DTO
 * 3. Транзакционность
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;  // создадим дальше

    /**
     * Регистрация нового пользователя
     */
    @Transactional
    public AuthResponse register(String name, String email, String password) {
        // Проверяем, что email не занят
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        // Создаем пользователя
        User user = new User(name, email, passwordEncoder.encode(password));
        user = userRepository.save(user);

        // Генерируем JWT токен
        String token = jwtService.generateToken(user.getEmail(),user.getId(),user.getName(),user.getRole());

        // Возвращаем ответ
        return new AuthResponse(token, user.getEmail(), user.getName(), user.getRole().name());
    }

    /**
     * Вход пользователя
     */
    public AuthResponse login(String email, String password) {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtService.generateToken(user.getEmail(),user.getId(),user.getName(),user.getRole());
        return new AuthResponse(token, user.getEmail(), user.getName(), user.getRole().name());
    }

    /**
     * Получение пользователя по ID (для других сервисов)
     */
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    /**
     * Получение пользователя по email
     */
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}
