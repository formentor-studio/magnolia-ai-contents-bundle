package org.formentor.magnolia.ai.infrastructure.openai;

import org.formentor.magnolia.ai.domain.AiModel;
import org.formentor.magnolia.ai.domain.Dataset;

import java.util.concurrent.CompletableFuture;

public class AiModelOpenAiImpl implements AiModel {

    @Override
    public CompletableFuture train(Dataset dataset) {
        // 1. Prepare dataset
        // 2. Upload file
        // 3. Train model
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture predict(Object prompt) {
        return null;
    }

    /**
     * Prepares dataset to train an OpenAI model
     * See:
     * - https://platform.openai.com/docs/guides/fine-tuning/preparing-your-dataset
     * - https://platform.openai.com/docs/guides/fine-tuning/case-study-product-description-based-on-a-technical-list-of-properties
     * @param dataset
     * @return
     */
    // TODO Remember to make this method "private", it is just "protected" during testing.
    protected String prepareDataset(Dataset dataset) {
        return dataset.getExamples().stream().reduce("", (acc, example) ->
            acc.concat(
                    new ExampleValue(example.getPrompt(), example.getCompletion()).jsonl()
            )
            , String::concat);
    }


}
