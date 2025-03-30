package com.tech.afa.archangel.library.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@NoArgsConstructor(access = AccessLevel.NONE)
public class SqlLoader {

    private static final String DIRECTORY = "sql/";

    public static String loadSql(String fileName) {
        try (InputStream inputStream = SqlLoader.class.getClassLoader().getResourceAsStream(DIRECTORY + fileName)) {
            if (inputStream == null) {
                throw new RuntimeException("File not found: " + fileName);
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("SQL-file loading error: " + fileName, e);
        }
    }
}
