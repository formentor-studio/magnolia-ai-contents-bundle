package org.formentor.ai.infrastructure.azure;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.formentor.magnolia.ai.infrastructure.openai.CompletionRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class CompletionRequestTest {

    static ObjectMapper mapper = buildMapper();

    @Test
    void mapJsonWithObject() throws JsonProcessingException {
        String json = "{\n" +
                "  \"prompt\": \"Write a hotel room description based on these characteristics:wifi, television, two beds, jacuzzi, large, balcony\",\n" +
                "  \"temperature\": 0.5,\n" +
                "  \"max_tokens\": 150,\n" +
                "  \"top_p\": 1.0,\n" +
                "  \"frequency_penalty\": 0.0,\n" +
                "  \"presence_penalty\": 0.0\n" +
                "}";

        CompletionRequest completionRequest = mapper.readValue(json, CompletionRequest.class);

        assertEquals("Write a hotel room description based on these characteristics:wifi, television, two beds, jacuzzi, large, balcony", completionRequest.getPrompt());
    }

    private static ObjectMapper buildMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        return mapper;
    }
}
