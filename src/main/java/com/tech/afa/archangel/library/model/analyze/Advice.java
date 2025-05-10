package com.tech.afa.archangel.library.model.analyze;

import com.tech.afa.archangel.library.model.enums.AdviceType;
import com.tech.afa.archangel.library.model.enums.Importance;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Advice {
    private String advice;
    private AdviceType adviceType;
    private Importance importance;

    @Override
    public String toString() {
        return String.format(
            """
                [%s][%s] %s
                """,
            adviceType.name(), importance.name(), advice
        );
    }
}
