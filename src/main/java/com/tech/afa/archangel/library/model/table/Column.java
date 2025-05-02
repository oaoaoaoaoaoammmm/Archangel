package com.tech.afa.archangel.library.model.table;

import com.tech.afa.archangel.library.model.enums.ConstraintType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class Column {
    private String name;
    private String type;
    private Set<ConstraintType> constraints;

    @Override
    public String toString() {
        return "name: " + name + " | type: " + type +
            (constraints.isEmpty() ? "" : " | constraints: " + constraints.stream()
                .map(Enum::name)
                .collect(Collectors.joining(", ")));
    }
}
