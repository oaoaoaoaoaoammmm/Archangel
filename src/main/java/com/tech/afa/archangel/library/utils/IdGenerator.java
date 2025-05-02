package com.tech.afa.archangel.library.utils;

public class IdGenerator {

    public String getId(String request) {
        return String.valueOf(request.hashCode());
    }
}
