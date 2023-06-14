package org.formentor.magnolia.ai.infrastructure.azure;

import org.formentor.magnolia.ai.AIContentsModule;
import org.formentor.magnolia.ai.domain.TextAiService;
import org.formentor.magnolia.ai.domain.TextPerformance;
import org.formentor.magnolia.ai.infrastructure.openai.CompletionRequest;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

public class TextAiServiceAzure implements TextAiService {

    private final int AZURE_OPENAI_MAX_TOKENS = 4096;
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
        /*
        Translate the following from slang to a business letter:
        'Dude, This is Joe, check out this spec on this standing lamp.'
         */
        String promptWithInstruction = String.format("%s:\n%s", instruction, prompt);

        return completeText(
                promptWithInstruction,
                AZURE_OPENAI_MAX_TOKENS - estimateTokensCount(promptWithInstruction),
                performance);
    }

    private int estimateTokensCount(String prompt) {
        /**
         * At 2023-06-14 based on https://help.openai.com/en/articles/4936856-what-are-tokens-and-how-to-count-them
         *
         * 1 token ~= 4 chars in English
         * 1 token ~= ¾ words
         * 100 tokens ~= 75 words
         * Or
         * 1-2 sentence ~= 30 tokens
         * 1 paragraph ~= 100 tokens
         * 1,500 words ~= 2048 tokens
         */

        return (int)(prompt.length()/4 * 1.15);  // Add 15% for security
    }
}
