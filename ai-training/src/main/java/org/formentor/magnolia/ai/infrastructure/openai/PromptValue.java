package org.formentor.magnolia.ai.infrastructure.openai;

public class PromptValue {
    public static final String PROMPT_SEPARATOR = "->";

    private final String value;

    public PromptValue(String value) {
        this.value = value;
    }

    public static PromptValue fromString(String value) {
        return new PromptValue(value);
    }

    public String toString() {
        return String.format("%s%s", value, PROMPT_SEPARATOR);
    }
}
