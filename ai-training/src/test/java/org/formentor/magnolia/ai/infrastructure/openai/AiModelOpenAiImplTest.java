package org.formentor.magnolia.ai.infrastructure.openai;

import org.formentor.magnolia.ai.domain.Dataset;
import org.formentor.magnolia.ai.domain.Example;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AiModelOpenAiImplTest {

    @Test
    void prepareDataset() {
        // TODO Apply Object Mother pattern to create example of source data  https://martinfowler.com/bliki/ObjectMother.html
        Dataset dataset = new Dataset();
        dataset.setExamples(Arrays.asList(
                Example.builder()
                        .prompt("name is Vietnam: Tradition and Today. location is Ho Chi Minh City, Vietnam. duration is 14.")
                        .completion("Vietnam is one of the world's most exotic and culturally rich destinations. A gem among gems, it offers dazzling diversity for visitors.")
                        .build(),
                Example.builder()
                        .prompt("name is Hut to Hut in the Swiss Alps. location is Zurich, Switzerland. duration is 7.")
                        .completion("Some Swiss people are going to be pretty annoyed with us for revealing their secrets. Hush..don&rsquo;t tell anyone, but there&rsquo;s more to Switzerland than skiing! And we don&rsquo;t mean shopping for watches and staying in fancy hotels.")
                        .build()
                ));
        AiModelOpenAiImpl aiModel = new AiModelOpenAiImpl();

        String actual = aiModel.prepareDataset(dataset);
        // FileUtils.writeStringToFile(new File("src/test/resources/borrar.jsonl"), actual, "utf-8");

        /* The "expected" value has been validated with openai tools
        $ openai tools fine_tunes.prepare_data -f <LOCAL_FILE>
         */
        String expected = "{\"prompt\":\"name is Vietnam: Tradition and Today. location is Ho Chi Minh City, Vietnam. duration is 14.->\", \"completion\": \" Vietnam is one of the world's most exotic and culturally rich destinations. A gem among gems, it offers dazzling diversity for visitors. END\"}\n" +
                "{\"prompt\":\"name is Hut to Hut in the Swiss Alps. location is Zurich, Switzerland. duration is 7.->\", \"completion\": \" Some Swiss people are going to be pretty annoyed with us for revealing their secrets. Hush..don&rsquo;t tell anyone, but there&rsquo;s more to Switzerland than skiing! And we don&rsquo;t mean shopping for watches and staying in fancy hotels. END\"}\n";
        assertEquals(expected, actual);
    }
}
