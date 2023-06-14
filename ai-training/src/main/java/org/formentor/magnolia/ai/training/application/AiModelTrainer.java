package org.formentor.magnolia.ai.training.application;

import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.PropertyUtil;
import lombok.extern.slf4j.Slf4j;
import org.formentor.magnolia.ai.training.domain.AiModel;
import org.formentor.magnolia.ai.training.domain.Dataset;
import org.formentor.magnolia.ai.training.domain.Example;
import org.formentor.magnolia.ai.training.domain.PropertyPromptValue;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import javax.inject.Inject;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.QueryManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class AiModelTrainer {
    private final AiModel aiModel;

    @Inject
    public AiModelTrainer(AiModel aiModel) {
        this.aiModel = aiModel;
    }

    public CompletableFuture<String> run(String modelName, Session session, String root, String nodeType, List<PropertyPromptValue> propertiesAsPrompt, String propertyAsCompletion) throws RepositoryException {
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
    private List<Example> buildTrainingData(Session session, String root, String nodeType, List<PropertyPromptValue> propertiesAsPrompt, String propertyAsCompletion) throws RepositoryException {
        List<Example> examples = new ArrayList<>();
        QueryManager queryManager = session.getWorkspace().getQueryManager();
        String select = String.format("SELECT * FROM [%s] WHERE ISDESCENDANTNODE('%s')", nodeType, root);
        queryManager.createQuery(select, "JCR-SQL2")
                .execute()
                .getNodes()
                .forEachRemaining(node -> buildExample((Node)node, propertiesAsPrompt, propertyAsCompletion).ifPresent((examples::add)));

        return examples;
    }

    private Optional<Example> buildExample(Node node, List<PropertyPromptValue> propertiesAsPrompt, String propertyAsCompletion) {
        /*
        {
            "prompt":"Item is a handbag. Colour is army green. Price is midrange. Size is small.",
            "completion":" This stylish small green handbag will add a unique touch to your look, without costing you a fortune."
        }
        */
        String prompt = propertiesAsPrompt.stream()
                .reduce("", (acc, current) -> {
                    Optional<String> propertyPromptString = getPropertyPromptString(node, current);
                    return propertyPromptString.map(value -> acc.concat(String.format("%s is %s. ", current.getPropertyName(), value))).orElse(acc);
                },
               String::concat)
            .trim();
        Optional<String> completion = getPropertyString(node, propertyAsCompletion).map(this::cleanHtml);
        return completion.map((value) -> Example.builder()
                .prompt(prompt)
                .completion(value)
                .build());
    }

    private Optional<String> getPropertyPromptString(Node node, PropertyPromptValue propertyPrompt) {
        Optional<String> propertyValueString = getPropertyString(node, propertyPrompt.getPropertyName());
        if (!propertyValueString.isPresent()) {
            return Optional.empty();
        }

        if (propertyPrompt.isReference()) {
            final String referenceNodeId = propertyValueString.get();
            final String referenceWorkspace = propertyPrompt.getReference().getTargetWorkspace();
            final String referencePropertyName = propertyPrompt.getReference().getTargetPropertyName();
            try {
                Session session = MgnlContext.getJCRSession(referenceWorkspace);
                Node referenceNode = session.getNodeByIdentifier(referenceNodeId);
                return getPropertyString(referenceNode, referencePropertyName);
            } catch (ItemNotFoundException e) {
                log.error("Can't find referenced node '{}' in workspace '{}' when building training example of {}", referenceNodeId, referenceWorkspace, node, e);
                return Optional.empty();
            } catch (RepositoryException e) {
                log.error("Errors reading property '{}' of the referenced node {} in workspace '{}' when building training example of {}", referencePropertyName, referenceNodeId, referenceWorkspace, node, e);
                return Optional.empty();
            }
        }

        return propertyValueString;
    }

    private Optional<String> getPropertyString(Node node, String propertyName) {
        return Optional.ofNullable(PropertyUtil.getString(node, propertyName)); // Using a decorator of PropertyUtil.getString() because I prefer working with "Optional" instead of "null"
    }

    private String cleanHtml(String rawText) {
        return Jsoup.clean(rawText, Safelist.none());
    }
}
