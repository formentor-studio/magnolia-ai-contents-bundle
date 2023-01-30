package org.formentor.magnolia.ai.application;

import info.magnolia.context.Context;
import info.magnolia.importexport.command.JcrImportCommand;
import info.magnolia.repository.RepositoryManager;
import info.magnolia.test.junit5.MagnoliaJcrTest;
import org.apache.commons.io.FileUtils;
import org.formentor.magnolia.ai.domain.AiModel;
import org.formentor.magnolia.ai.domain.Dataset;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@MagnoliaJcrTest
public class AiModelTrainerTest {

    @Test
    void shouldTrainModel(RepositoryManager repositoryManager) throws RepositoryException {
        final String ORIGIN_REPOSITORY = "magnolia";
        final String ORIGIN_WORKSPACE = "tours";

        // Given
        repositoryManager.createWorkspace(ORIGIN_REPOSITORY, ORIGIN_WORKSPACE);
        Repository repository = repositoryManager.getRepository(ORIGIN_REPOSITORY);
        Session session = repositoryManager.getRepository(ORIGIN_REPOSITORY).login(ORIGIN_WORKSPACE);
        // TODO Apply Object Mother pattern to create example of source data  https://martinfowler.com/bliki/ObjectMother.html
        Node content = session.getRootNode()
                .addNode("magnolia-travels", "mgnl:folder")
                .addNode("Vietnam--Tradition-and-Today", "mgnl:content");
        content.setProperty("name", "Vietnam: Tradition and Today");
        content.setProperty("location", "Ho Chi Minh City, Vietnam");
        content.setProperty("duration", "14");
        content.setProperty("body", "Vietnam is one of the world's most exotic and culturally rich destinations. A gem among gems, it offers dazzling diversity for visitors.");
        session.save();

        AiModel aiModel = mock(AiModel.class);
        final AiModelTrainer aiModelTrainer = new AiModelTrainer(repository, aiModel);

        // When
        aiModelTrainer.run(ORIGIN_WORKSPACE, "/magnolia-travels", "mgnl:content", Arrays.asList("name", "location", "duration"), "body").join();

        // Then
        ArgumentCaptor<Dataset> argumentCaptor = ArgumentCaptor.forClass(Dataset.class);
        verify(aiModel).train(argumentCaptor.capture());
        Dataset actual = argumentCaptor.getValue();
        assertEquals("name is Vietnam: Tradition and Today. location is Ho Chi Minh City, Vietnam. duration is 14.", actual.getExamples().get(0).getPrompt());
        assertEquals("Vietnam is one of the world's most exotic and culturally rich destinations. A gem among gems, it offers dazzling diversity for visitors.", actual.getExamples().get(0).getCompletion());
    }

    @Test
    void importContent(Context context, RepositoryManager repositoryManager) throws Exception {
        repositoryManager.createWorkspace("magnolia", "tours");
        File file = new File("src/test/resources/contents/tours.magnolia-travels.yaml");
        JcrImportCommand command = new JcrImportCommand();
        command.setFileName(file.getName());
        command.setRepository("tours");
        command.setPath("/");
        command.setStream(new ByteArrayInputStream(FileUtils.readFileToByteArray(file)));
        command.execute(context);

        Session session = context.getJCRSession("tours");
        Node tour = session.getNode("/magnolia-travels/Vietnam--Tradition-and-Today");
        assertEquals("Magnolia Travels", tour.getProperty("author").getString());
    }
}
