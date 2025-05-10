package com.tech.afa.archangel.library.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.InputStream;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLXML;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SQLUtils {

    public static String extractFieldName(String field) {
        return Arrays.stream(field.split("\\."))
            .reduce((first, last) -> last)
            .orElse("");
    }

    public static String extractSchemaAndFieldName(String input) {
        String[] parts = input.split("\\.");
        if (parts.length == 3) {
            return parts[1] + "." + parts[2];
        } else if (parts.length == 2 || parts.length == 1) {
            return input;
        } else {
            return parts[parts.length - 2] + "." + parts[parts.length - 1];
        }
    }

    public static String resolveSqlParametersByName(String sql, Map<String, Object> parameters) {
        String resolved = sql;
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            String value = format(entry.getValue());
            resolved = resolved.replace(entry.getKey(), value);
        }
        return resolved;
    }

    public static String resolveSqlParametersByIndex(String sql, Map<Integer, Object> parameters) {
        String resolved = sql;
        for (Map.Entry<Integer, Object> entry : parameters.entrySet()) {
            String value = format(entry.getValue());
            resolved = resolved.replaceFirst("\\?", value);
        }
        return resolved;
    }

    private static String format(Object value) {
        if (value == null) {
            return "NULL";
        }
        if (value instanceof String
            || value instanceof java.util.Date
            || value instanceof TemporalAccessor
            || value instanceof UUID
            || value instanceof Enum) {
            return "'" + value.toString() + "'";
        }
        if (value instanceof byte[] || value instanceof InputStream || value instanceof Blob) {
            return "'<binary>'";
        }
        if (value instanceof Reader || value instanceof Clob) {
            return "'<text>'";
        }
        if (value instanceof SQLXML) {
            return "'<sql-xml>'";
        }
        if (value.getClass().getName().equals("org.postgresql.util.PGobject")) {
            return "'<pg-object>'";
        }
        return value.toString();
    }
}
