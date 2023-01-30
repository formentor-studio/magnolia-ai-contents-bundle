package org.formentor.magnolia.ai.application;

import com.machinezoo.noexception.Exceptions;
import org.formentor.magnolia.ai.domain.AiModel;
import org.formentor.magnolia.ai.domain.Dataset;
import org.formentor.magnolia.ai.domain.Example;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.query.QueryManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AiModelTrainer {
    private final Repository repository;
    private final AiModel aiModel;

    public AiModelTrainer(Repository repository, AiModel aiModel) {
        this.repository = repository;
        this.aiModel = aiModel;
    }

    public CompletableFuture<String> run(String workspace, String root, String nodeType, List<String> propertiesAsPrompt, String propertyAsCompletion) throws RepositoryException {
        Dataset dataset = new Dataset();
        dataset.setExamples(buildTrainingData(workspace, root, nodeType, propertiesAsPrompt, propertyAsCompletion));

        aiModel.train(dataset);
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Builds training dataset
     * @param workspace
     * @param root
     * @param nodeType
     * @param propertiesAsPrompt
     * @param propertyAsCompletion
     * @return
     */
    private List<Example> buildTrainingData(String workspace, String root, String nodeType, List<String> propertiesAsPrompt, String propertyAsCompletion) throws RepositoryException {
        List<Example> examples = new ArrayList<>();
        QueryManager queryManager = repository.login(workspace).getWorkspace().getQueryManager();
        String select = String.format("SELECT * FROM [%s] WHERE ISDESCENDANTNODE('%s')", nodeType, root);
        queryManager.createQuery(select, "JCR-SQL2")
                .execute()
                .getNodes()
                .forEachRemaining(node -> {
                    examples.add(buildExample((Node)node, propertiesAsPrompt, propertyAsCompletion));
                });

        return examples;
    }

    private Example buildExample(Node node, List<String> propertiesAsPrompt, String propertyAsCompletion) {
        /*
        {
            "prompt":"Item is a handbag. Colour is army green. Price is midrange. Size is small.",
            "completion":" This stylish small green handbag will add a unique touch to your look, without costing you a fortune."
        }
        */
        String prompt = propertiesAsPrompt.stream()
                .reduce("", (acc, current) ->
                   acc.concat(String.format("%s is %s. ", current, Exceptions.wrap().get(() -> node.getProperty(current).getString())))
                ).trim();
        String completion = Exceptions.wrap().get(() -> node.getProperty(propertyAsCompletion).getString());
        return Example.builder()
                .prompt(prompt)
                .completion(completion)
                .build();
    }
}
