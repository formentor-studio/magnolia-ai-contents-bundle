package org.formentor.ai.infrastructure.azure;

import org.formentor.ai.infrastructure.openai.CompletionResultMother;
import org.formentor.magnolia.ai.AIContentsModule;
import org.formentor.magnolia.ai.domain.TextPerformance;
import org.formentor.magnolia.ai.infrastructure.azure.AzureOpenAiApi;
import org.formentor.magnolia.ai.infrastructure.azure.AzureOpenAiApiClientProvider;
import org.formentor.magnolia.ai.infrastructure.azure.TextAiServiceAzure;
import org.formentor.magnolia.ai.infrastructure.openai.CompletionRequest;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TextAiServiceAzureTest {

    @Test
    void completeTextCallsToAzureOpenAI() {
        AzureOpenAiApi azureOpenAiApi = mock(AzureOpenAiApi.class);
        when(azureOpenAiApi.createCompletion(anyString(), any())).thenReturn(CompletionResultMother.fixed());
        AzureOpenAiApiClientProvider apiClientProvider = mock(AzureOpenAiApiClientProvider.class);
        when(apiClientProvider.get()).thenReturn(azureOpenAiApi);

        AIContentsModule.Azure azureDefinition = new AIContentsModule.Azure();
        azureDefinition.setApiVersion("2023-05-15");
        AIContentsModule aiContentsModule = new AIContentsModule();
        aiContentsModule.setAzure(azureDefinition);

        TextAiServiceAzure textAiServiceAzure = new TextAiServiceAzure(apiClientProvider, aiContentsModule);

        textAiServiceAzure.completeText("Write a hotel room description based on these characteristics:wifi, television, two beds, jacuzzi, large, balcony", 200, TextPerformance.best).join();

        verify(azureOpenAiApi).createCompletion(anyString(), any(CompletionRequest.class));
    }
}
