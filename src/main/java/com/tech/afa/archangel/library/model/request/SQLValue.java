package com.tech.afa.archangel.library.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SQLValue {
    private String rawValue;
    private SQLRequest selectRequest;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("SQLValue {");
        if (rawValue != null) sb.append("rawValue='").append(rawValue).append("'");
        if (selectRequest != null) sb.append(sb.length() > 10 ? " " : "").append("selectRequest=").append(selectRequest);
        return sb.append("}").toString();
    }
}
