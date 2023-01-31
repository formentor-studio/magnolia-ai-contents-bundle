package org.formentor.magnolia.ai.infrastructure.openai;

public class CompletionValue {
    public static final String COMPLETION_ENDING = " END";

    private final String value;

    public CompletionValue(String value) {
        this.value = value;
    }

    public static CompletionValue fromString(String value) {
        return new CompletionValue(value);
    }

    public String toString() {
        return String.format("%s%s", value, COMPLETION_ENDING);
    }
}
