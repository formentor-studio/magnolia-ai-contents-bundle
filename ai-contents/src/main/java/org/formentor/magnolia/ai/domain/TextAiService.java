package org.formentor.magnolia.ai.domain;

import java.util.concurrent.CompletableFuture;

public interface TextAiService {
    default CompletableFuture<String> completeText(String prompt, Integer words, TextPerformance performance) {
        throw new RuntimeException("Unsupported operation completeText(String, Integer)");
    }
    CompletableFuture<String> editText(String prompt,TextPerformance performance,String instruction);

}
