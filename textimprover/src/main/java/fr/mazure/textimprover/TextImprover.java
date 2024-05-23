package fr.mazure.textimprover;

import dev.langchain4j.model.openai.OpenAiChatModel;

public class TextImprover {

    public static void main(String[] args) {

        OpenAiChatModel model = OpenAiChatModel.withApiKey("demo");

        String joke = model.generate("What is the meaning of life?");

        System.out.println(joke);
    }
}
