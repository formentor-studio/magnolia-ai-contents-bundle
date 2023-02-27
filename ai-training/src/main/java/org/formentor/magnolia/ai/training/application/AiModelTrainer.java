package org.formentor.magnolia.ai.training.application;

import com.machinezoo.noexception.Exceptions;
import org.formentor.magnolia.ai.training.domain.AiModel;
import org.formentor.magnolia.ai.training.domain.Dataset;
import org.formentor.magnolia.ai.training.domain.Example;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.QueryManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AiModelTrainer {
    private final AiModel aiModel;

    @Inject
    public AiModelTrainer(AiModel aiModel) {
        this.aiModel = aiModel;
    }

    public CompletableFuture<String> run(String modelName, Session session, String root, String nodeType, List<String> propertiesAsPrompt, String propertyAsCompletion) throws RepositoryException {
        Dataset dataset = new Dataset();
        dataset.setExamples(buildTrainingData(session, root, nodeType, propertiesAsPrompt, propertyAsCompletion));

        return aiModel.train(modelName, dataset);
    }

    /**
     * Builds training dataset
     * @param root
     * @param nodeType
     * @param propertiesAsPrompt
     * @param propertyAsCompletion
     * @return
     */
    private List<Example> buildTrainingData(Session session, String root, String nodeType, List<String> propertiesAsPrompt, String propertyAsCompletion) throws RepositoryException {
        List<Example> examples = new ArrayList<>();
        QueryManager queryManager = session.getWorkspace().getQueryManager();
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
                   acc.concat(String.format("%s is %s. ", current, Exceptions.wrap().get(() -> cleanHtml(node.getProperty(current).getString()))))
                ).trim();
        String completion = Exceptions.wrap().get(() -> cleanHtml(node.getProperty(propertyAsCompletion).getString()));
        return Example.builder()
                .prompt(prompt)
                .completion(completion)
                .build();
    }

    private String cleanHtml(String rawText) {
        return Jsoup.clean(rawText, Safelist.none());
    }
}
