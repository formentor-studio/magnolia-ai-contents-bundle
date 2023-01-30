package org.formentor.magnolia.ai.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Example {
    private String prompt;
    private String completion;
}
