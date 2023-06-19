package org.formentor.magnolia.ai.ui.field;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TextAIFieldPromptDefinition {
    public TextAIFieldPromptDefinition() {

    }

    private String propertyName;
    private Integer limit;
    private String targetWorkspace;
    private String targetPropertyName;

    public boolean isReference() {
        return (targetWorkspace != null && targetPropertyName != null);
    }
}
