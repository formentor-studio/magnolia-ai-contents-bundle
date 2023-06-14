package org.formentor.magnolia.ai.training.domain;

import lombok.Data;

import java.util.Optional;

@Data
public class PropertyPromptValue {
    public enum PropertyType { string, array }
    private String propertyName;
    private PropertyType propertyType;
    private Integer limit;
    private Reference reference; // Inspired by Delivery API references. https://docs.magnolia-cms.com/product-docs/6.2/Developing/API/Delivery-API/Resolving-references-with-the-delivery-endpoint.html

    public PropertyType getPropertyType() {
        return Optional.ofNullable(propertyType).orElse(PropertyType.string);
    }

    public boolean isReference() {
        return (reference != null && reference.getTargetWorkspace() != null && reference.getTargetPropertyName() != null);
    }

    @Data
    public static class Reference {
        private String targetWorkspace;
        private String targetPropertyName;
    }
}
