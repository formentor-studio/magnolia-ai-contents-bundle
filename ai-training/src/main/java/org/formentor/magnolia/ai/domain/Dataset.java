package org.formentor.magnolia.ai.domain;

import lombok.Data;

import java.util.List;

@Data
public class Dataset {
    private List<Example> examples;
}
