package org.formentor.ai.infrastructure.azure;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.formentor.magnolia.ai.infrastructure.openai.CompletionRequest;
import org.formentor.magnolia.ai.infrastructure.openai.CompletionResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class CompletionResultTest {

    static ObjectMapper mapper = buildMapper();

    @Test
    void mapJsonWithObject() throws JsonProcessingException {
        String json = "{\n" +
                "  \"id\": \"cmpl-7P3bC2Uj4G78SSaEVsS0kTYpofNZl\",\n" +
                "  \"object\": \"text_completion\",\n" +
                "  \"created\": 1686206390,\n" +
                "  \"model\": \"text-davinci-003\",\n" +
                "  \"choices\": [\n" +
                "    {\n" +
                "      \"text\": \"\\n\\nThis luxurious room offers a large space with two comfortable beds, a flat-screen television, and access to high-speed wifi. Enjoy a relaxing soak in the jacuzzi tub, or step out onto the spacious balcony to take in the beautiful views.\",\n" +
                "      \"index\": 0,\n" +
                "      \"finish_reason\": \"stop\",\n" +
                "      \"logprobs\": null\n" +
                "    }\n" +
                "  ],\n" +
                "  \"usage\": { \"completion_tokens\": 54, \"prompt_tokens\": 26, \"total_tokens\": 80 }\n" +
                "}";

        CompletionResult completionResult = mapper.readValue(json, CompletionResult.class);

        assertEquals("\n\nThis luxurious room offers a large space with two comfortable beds, a flat-screen television, and access to high-speed wifi. Enjoy a relaxing soak in the jacuzzi tub, or step out onto the spacious balcony to take in the beautiful views.", completionResult.getChoices().get(0).getText());
    }

    private static ObjectMapper buildMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        return mapper;
    }
}
