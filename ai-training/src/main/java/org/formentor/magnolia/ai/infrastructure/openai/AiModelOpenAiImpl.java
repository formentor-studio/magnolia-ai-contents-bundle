package org.formentor.magnolia.ai.infrastructure.openai;

import org.formentor.magnolia.ai.domain.AiModel;
import org.formentor.magnolia.ai.domain.Dataset;
import org.formentor.magnolia.ai.domain.Example;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AiModelOpenAiImpl implements AiModel {
    public static final String PROMPT_SEPARATOR = "->";

    @Override
    public CompletableFuture train(Dataset dataset) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture predict(Object prompt) {
        return null;
    }

    /**
     * SUPER IMPORTANTE
     *
     * - El separador de línea de .jsonl debe ser "\n" Ver https://jsonlines.org/
     * - Al final de cada prompt hay que incluir un separador e.g "->" Ver https://beta.openai.com/docs/guides/fine-tuning/case-study-customer-support-chatbot
     */
    private void createJSONL(List<Example> examples) {

    }
}
