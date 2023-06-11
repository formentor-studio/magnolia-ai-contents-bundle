package org.formentor.ai.infrastructure.azure;

import org.formentor.magnolia.ai.AIContentsModule;
import org.formentor.magnolia.ai.infrastructure.azure.AzureApiKeyProvider;
import org.formentor.magnolia.ai.infrastructure.azure.AzureOpenAiApi;
import org.formentor.magnolia.ai.infrastructure.azure.AzureOpenAiApiClientProvider;
import org.formentor.magnolia.ai.infrastructure.openai.CompletionRequest;
import org.formentor.magnolia.ai.infrastructure.openai.CompletionResult;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AzureOpenAiApiClientProviderTest {

    @Disabled
    @Test
    void createCompletion() {
        AIContentsModule.Azure azureDefinition = new AIContentsModule.Azure();
        azureDefinition.setHost("openai.azure.com");
        azureDefinition.setResource("aoaipocmhi-mgnl");
        azureDefinition.setDeployment("text-davinci-003");

        AIContentsModule definition = new AIContentsModule();
        definition.setAzure(azureDefinition);

        AzureApiKeyProvider azureApiKeyProvider = mock(AzureApiKeyProvider.class);
        when(azureApiKeyProvider.get()).thenReturn("88**********");
        final AzureOpenAiApiClientProvider clientProvider = new AzureOpenAiApiClientProvider(definition, azureApiKeyProvider);

        AzureOpenAiApi api = clientProvider.get();

        CompletionRequest request = CompletionRequest.builder()
                .prompt("Write a hotel room description based on these characteristics:wifi, television, two beds, jacuzzi, large, balcony")
                .temperature(0.5)
                .max_tokens(150)
                .top_p(1.0)
                .frequency_penalty(0.0)
                .presence_penalty(0.0)
                .build();

        CompletionResult result = api.createCompletion("2023-05-15", request);

        assertNotNull(result);
    }

}