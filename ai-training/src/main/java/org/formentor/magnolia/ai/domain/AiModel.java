package org.formentor.magnolia.ai.domain;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AiModel {
    CompletableFuture train(Dataset dataset);
    CompletableFuture predict(Object prompt);
}
