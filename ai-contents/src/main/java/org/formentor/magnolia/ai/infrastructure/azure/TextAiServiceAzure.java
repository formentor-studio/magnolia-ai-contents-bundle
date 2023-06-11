package org.formentor.magnolia.ai.infrastructure.azure;

import org.formentor.magnolia.ai.AIContentsModule;
import org.formentor.magnolia.ai.domain.TextAiService;
import org.formentor.magnolia.ai.domain.TextPerformance;
import org.formentor.magnolia.ai.infrastructure.openai.CompletionRequest;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

public class TextAiServiceAzure implements TextAiService {

    private final String apiVersion;
    private final AzureOpenAiApi api;

    @Inject
    public TextAiServiceAzure(AzureOpenAiApiClientProvider apiClientProvider, AIContentsModule definition) {
        this(apiClientProvider, definition.getAzure() == null? null: definition.getAzure().getApiVersion());
    }

    public TextAiServiceAzure(AzureOpenAiApiClientProvider apiClientProvider, String apiVersion) {
        this.api = apiClientProvider.get();
        this.apiVersion = apiVersion;
    }

    @Override
    public CompletableFuture<String> completeText(String prompt, Integer words, TextPerformance performance) {
        CompletionRequest request = CompletionRequest.builder()
                .prompt(prompt)
                .temperature(0.5)
                .max_tokens(words)
                .top_p(1.0)
                .frequency_penalty(0.0)
                .presence_penalty(0.0)
                .build();

        return CompletableFuture.supplyAsync(() -> api.createCompletion(apiVersion, request))
                .thenApply(completionResult -> completionResult.getChoices().get(0).getText());
    }

    @Override
    public CompletableFuture<String> editText(String prompt, TextPerformance performance, String instruction) {
        throw new RuntimeException("Unsupported operation (editText)");
    }
}
