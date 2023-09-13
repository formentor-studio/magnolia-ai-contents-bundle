package org.formentor.ai.infrastructure.openai;

import org.formentor.magnolia.ai.domain.TextPerformance;
import org.formentor.magnolia.ai.infrastructure.openai.CompletionRequest;
import org.formentor.magnolia.ai.infrastructure.openai.CompletionResult;
import org.formentor.magnolia.ai.infrastructure.openai.OpenAiApi;
import org.formentor.magnolia.ai.infrastructure.openai.OpenAiApiClientProvider;
import org.formentor.magnolia.ai.infrastructure.openai.TextAiServiceOpenAi;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TextAiServiceOpenAITest {

    private final static String COMPLETION_TEXT_EXAMPLE = "\n\nIntroducing the Chair Brown, a classic mid-century inspired office chair that will bring a timeless look to any workspace. Crafted from wood and metal, this chair is part of a beautiful family of mid-century inspired office furniture that includes filing cabinets, desks, bookcases, meeting tables, and more. Its classic design and durable materials make it a perfect fit for any home or office. Add the Chair Brown to your workspace and enjoy the timeless look and feel of mid-century design.";

    @Test
    void CompleteTextConsumesOpenAiApi() {
        final String prompt = "i-am-a-prompt";
        final OpenAiApi openAiApi = mockOpenAiApi("nonce");
        final OpenAiApiClientProvider openAiApiClientProvider = mock(OpenAiApiClientProvider.class);
        when(openAiApiClientProvider.get()).thenReturn(openAiApi);

        TextAiServiceOpenAi textAiServiceOpenAi = new TextAiServiceOpenAi(openAiApiClientProvider);
        textAiServiceOpenAi.completeText(prompt, 250, TextPerformance.best).join();

        ArgumentCaptor<CompletionRequest> completionRequestArgumentCaptor = ArgumentCaptor.forClass(CompletionRequest.class);
        verify(openAiApi, times(1)).createCompletion(completionRequestArgumentCaptor.capture());
        assertEquals(prompt, completionRequestArgumentCaptor.getValue().getPrompt());
    }

    @Test
    void CompleteTextRemovesStartingLineFeedsFromText() throws ExecutionException, InterruptedException {
        final String prompt = "i-am-the-prompt";
        final String completionTextFromOpenAI = COMPLETION_TEXT_EXAMPLE;

        final OpenAiApi openAiApi = mockOpenAiApi(completionTextFromOpenAI);
        final OpenAiApiClientProvider openAiApiClientProvider = mock(OpenAiApiClientProvider.class);
        when(openAiApiClientProvider.get()).thenReturn(openAiApi);

        TextAiServiceOpenAi textAiServiceOpenAi = new TextAiServiceOpenAi(openAiApiClientProvider);
        String completionText = textAiServiceOpenAi.completeText(prompt, 250, TextPerformance.best).get();

        String completionTextWithoutBreaks = "Introducing the Chair Brown, a classic mid-century inspired office chair that will bring a timeless look to any workspace. Crafted from wood and metal, this chair is part of a beautiful family of mid-century inspired office furniture that includes filing cabinets, desks, bookcases, meeting tables, and more. Its classic design and durable materials make it a perfect fit for any home or office. Add the Chair Brown to your workspace and enjoy the timeless look and feel of mid-century design.";
        assertEquals(completionTextWithoutBreaks, completionText);
    }

    private OpenAiApi mockOpenAiApi(String completionText) {
        OpenAiApi api = mock(OpenAiApi.class);

        CompletionResult completionResult = new CompletionResult();
        CompletionResult.CompletionChoice completionChoice = new CompletionResult.CompletionChoice();
        completionChoice.setText(completionText);
        completionResult.setChoices(List.of(completionChoice));

        when(api.createCompletion(any())).thenReturn(completionResult);

        return api;
    }
}
