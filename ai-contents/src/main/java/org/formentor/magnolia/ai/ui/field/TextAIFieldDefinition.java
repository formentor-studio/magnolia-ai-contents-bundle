package org.formentor.magnolia.ai.ui.field;

import info.magnolia.ui.field.FieldType;
import info.magnolia.ui.field.TextFieldDefinition;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Field definition for Image AI.
 */
@FieldType("textFieldAI")
@Getter
@Setter
@Slf4j
public class TextAIFieldDefinition extends TextFieldDefinition {
    private Integer words;
    private String performance;
    private String strategy;
    private String template;
    private List<TextAIFieldPromptDefinition> prompt = new ArrayList<>();

    public TextAIFieldDefinition() {
        setFactoryClass(TextAIFieldFactory.class);
    }

}
