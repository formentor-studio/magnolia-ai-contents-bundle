package org.formentor.ai.infrastructure.openai;

import org.formentor.magnolia.ai.infrastructure.openai.CompletionResult;

import java.util.ArrayList;

public class CompletionResultMother {
    public static CompletionResult fixed() {
        CompletionResult completionResult = new CompletionResult();
        completionResult.setChoices(new ArrayList<>());
        completionResult.getChoices().add(new CompletionResult.CompletionChoice());

        return completionResult;
    }
}
