package com.todo.common.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class SecretService {
    private static final Logger log = LoggerFactory.getLogger(SecretService.class);
    private static final String SECRETS_PATH = "/run/secrets/";

    /**
     * Читает секрет из файла в /run/secrets/
     * @param secretName имя секрета (например, "jwt_secret")
     * @return содержимое файла (первая строка, обрезанная)
     * @throws IllegalStateException если файл не найден или не читается
     */
    public static String getSecret(String secretName) {
        String path = SECRETS_PATH + secretName;

        try (BufferedReader reader = new BufferedReader(
                new FileReader(path, StandardCharsets.UTF_8))) {

            String secret = reader.readLine();
            if (secret == null) {
                throw new IllegalStateException("Secret file is empty: " + path);
            }

            log.debug("Secret '{}' loaded successfully", secretName);
            return secret.trim(); // убираем лишние пробелы/переносы

        } catch (IOException e) {
            log.error("Failed to read secret file: {}", path, e);
            throw new IllegalStateException("Cannot read secret: " + secretName, e);
        }
    }
}
